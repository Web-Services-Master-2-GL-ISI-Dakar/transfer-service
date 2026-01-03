package sn.ondmoney.txe.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.domain.enumeration.TransactionStatus;
import sn.ondmoney.txe.domain.enumeration.WalletStatus;
import sn.ondmoney.txe.kafka.TransferEventProducer;
import sn.ondmoney.txe.repository.TransferRepository;
import sn.ondmoney.txe.repository.WalletRepository;
import sn.ondmoney.txe.service.P2PTransferService;
import sn.ondmoney.txe.service.dto.P2PTransferRequest;
import sn.ondmoney.txe.service.dto.P2PTransferResponse;
import sn.ondmoney.txe.service.dto.WalletBalanceResponse;
import sn.ondmoney.txe.web.rest.errors.BadRequestAlertException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Service Implementation for P2P transfers.
 */
@Service
@Transactional
public class P2PTransferServiceImpl implements P2PTransferService {

    private static final Logger LOG = LoggerFactory.getLogger(P2PTransferServiceImpl.class);
    
    // Fee configuration - could be externalized to config
    private static final BigDecimal FEE_PERCENTAGE = new BigDecimal("0.01"); // 1%
    private static final BigDecimal MIN_FEE = new BigDecimal("25"); // 25 XOF minimum
    private static final BigDecimal MAX_FEE = new BigDecimal("5000"); // 5000 XOF maximum

    private final WalletRepository walletRepository;
    private final TransferRepository transferRepository;
    private final TransferEventProducer transferEventProducer;

    public P2PTransferServiceImpl(
            WalletRepository walletRepository,
            TransferRepository transferRepository,
            TransferEventProducer transferEventProducer) {
        this.walletRepository = walletRepository;
        this.transferRepository = transferRepository;
        this.transferEventProducer = transferEventProducer;
    }

    @Override
    public P2PTransferResponse initiateTransfer(String senderPhone, P2PTransferRequest request, String correlationId) {
        LOG.info("Initiating P2P transfer from {} to {}, amount: {}", 
            maskPhone(senderPhone), maskPhone(request.getReceiverPhone()), request.getAmount());

        // Validate sender and receiver are different
        if (senderPhone.equals(request.getReceiverPhone())) {
            throw new BadRequestAlertException("Cannot transfer to yourself", "transfer", "SELF_TRANSFER");
        }

        // Find sender wallet
        Wallet senderWallet = walletRepository.findByPhone(senderPhone)
            .orElseThrow(() -> new BadRequestAlertException("Sender wallet not found", "transfer", "SENDER_NOT_FOUND"));

        // Validate sender wallet is active
        if (senderWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BadRequestAlertException("Sender wallet is not active", "transfer", "SENDER_WALLET_INACTIVE");
        }

        // Find receiver wallet
        Wallet receiverWallet = walletRepository.findByPhone(request.getReceiverPhone())
            .orElseThrow(() -> new BadRequestAlertException("Receiver not found", "transfer", "RECEIVER_NOT_FOUND"));

        // Validate receiver wallet is active
        if (receiverWallet.getStatus() != WalletStatus.ACTIVE) {
            throw new BadRequestAlertException("Receiver wallet is not active", "transfer", "RECEIVER_WALLET_INACTIVE");
        }

        // Calculate fees
        BigDecimal fees = calculateFees(request.getAmount());
        BigDecimal totalToDebit = request.getAmount().add(fees);

        // Validate sufficient balance
        if (senderWallet.getBalance().compareTo(totalToDebit) < 0) {
            throw new BadRequestAlertException("Insufficient balance", "transfer", "INSUFFICIENT_BALANCE");
        }

        // Generate transaction ID
        String txId = "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        // Create transfer record
        Transfer transfer = new Transfer();
        transfer.setTxId(txId);
        transfer.setStatus(TransactionStatus.PENDING);
        transfer.setAmount(request.getAmount());
        transfer.setFees(fees);
        transfer.setSenderPhone(senderPhone);
        transfer.setReceiverPhone(request.getReceiverPhone());
        transfer.setInitiatedAt(Instant.now());
        transfer.setSender(senderWallet);
        transfer.setReceiver(receiverWallet);

        // Save transfer with PENDING status
        transfer = transferRepository.save(transfer);

        // Publish transfer.initiated event
        transferEventProducer.publishTransferInitiated(transfer, senderWallet, receiverWallet, correlationId);

        try {
            // Perform the transfer
            BigDecimal senderOldBalance = senderWallet.getBalance();
            BigDecimal receiverOldBalance = receiverWallet.getBalance();

            // Debit sender
            BigDecimal senderNewBalance = senderOldBalance.subtract(totalToDebit);
            senderWallet.setBalance(senderNewBalance);
            senderWallet.setVersion(senderWallet.getVersion() + 1);
            senderWallet.setUpdatedAt(Instant.now());
            walletRepository.save(senderWallet);

            // Credit receiver
            BigDecimal receiverNewBalance = receiverOldBalance.add(request.getAmount());
            receiverWallet.setBalance(receiverNewBalance);
            receiverWallet.setVersion(receiverWallet.getVersion() + 1);
            receiverWallet.setUpdatedAt(Instant.now());
            walletRepository.save(receiverWallet);

            // Update transfer status to COMPLETED
            transfer.setStatus(TransactionStatus.COMPLETED);
            transfer.setCompletedAt(Instant.now());
            transfer = transferRepository.save(transfer);

            // Publish transfer.completed event
            transferEventProducer.publishTransferCompleted(
                transfer, senderWallet, receiverWallet,
                senderNewBalance, receiverNewBalance, correlationId
            );

            LOG.info("P2P transfer completed successfully: {}", txId);

            return P2PTransferResponse.builder()
                .transactionId(txId)
                .status(TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .fees(fees)
                .totalDebited(totalToDebit)
                .senderPhone(senderPhone)
                .receiverPhone(request.getReceiverPhone())
                .currency(senderWallet.getCurrency())
                .newBalance(senderNewBalance)
                .description(request.getDescription())
                .initiatedAt(transfer.getInitiatedAt())
                .completedAt(transfer.getCompletedAt())
                .message("Transfert effectué avec succès")
                .build();

        } catch (Exception e) {
            LOG.error("P2P transfer failed: {}", e.getMessage(), e);

            // Update transfer status to FAILED
            transfer.setStatus(TransactionStatus.FAILED);
            transfer.setFailedAt(Instant.now());
            transfer.setErrorMessage(e.getMessage());
            transferRepository.save(transfer);

            // Publish transfer.failed event
            transferEventProducer.publishTransferFailed(
                transfer, senderWallet, receiverWallet,
                "TRANSFER_ERROR", e.getMessage(), correlationId
            );

            throw new BadRequestAlertException("Transfer failed: " + e.getMessage(), "transfer", "TRANSFER_FAILED");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WalletBalanceResponse getWalletBalance(String phoneNumber) {
        LOG.debug("Getting wallet balance for: {}", maskPhone(phoneNumber));

        Wallet wallet = walletRepository.findByPhone(phoneNumber)
            .orElseThrow(() -> new BadRequestAlertException("Wallet not found", "wallet", "WALLET_NOT_FOUND"));

        return WalletBalanceResponse.builder()
            .walletId("wal_" + wallet.getId())
            .phoneNumber(wallet.getPhone())
            .balance(wallet.getBalance())
            .availableBalance(wallet.getBalance()) // For now, same as balance
            .currency(wallet.getCurrency())
            .status(wallet.getStatus())
            .lastUpdated(wallet.getUpdatedAt() != null ? wallet.getUpdatedAt() : wallet.getCreatedAt())
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public WalletBalanceResponse getWalletDetails(String phoneNumber) {
        // Same as getWalletBalance for now, could be extended with more details
        return getWalletBalance(phoneNumber);
    }

    /**
     * Calculate transfer fees.
     * Fee structure: 1% of amount, minimum 25 XOF, maximum 5000 XOF
     */
    private BigDecimal calculateFees(BigDecimal amount) {
        BigDecimal calculatedFee = amount.multiply(FEE_PERCENTAGE);
        
        if (calculatedFee.compareTo(MIN_FEE) < 0) {
            return MIN_FEE;
        }
        if (calculatedFee.compareTo(MAX_FEE) > 0) {
            return MAX_FEE;
        }
        return calculatedFee.setScale(0, java.math.RoundingMode.CEILING);
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
}
