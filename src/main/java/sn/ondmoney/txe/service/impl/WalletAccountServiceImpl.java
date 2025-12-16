package sn.ondmoney.txe.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sn.ondmoney.txe.client.UserClient;
import sn.ondmoney.txe.domain.OperationLog;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.domain.enumeration.WalletStatus;
import sn.ondmoney.txe.repository.OperationLogRepository;
import sn.ondmoney.txe.repository.WalletRepository;
import sn.ondmoney.txe.service.api.AccountService;
import sn.ondmoney.txe.service.dto.BalanceDTO;
import sn.ondmoney.txe.service.dto.OperationResultDTO;
import sn.ondmoney.txe.service.dto.UserDTO;
import sn.ondmoney.txe.service.exception.AccountNotFoundException;


@Service
@Transactional
public class WalletAccountServiceImpl implements AccountService {

    private final Logger log = LoggerFactory.getLogger(WalletAccountServiceImpl.class);

    private final WalletRepository walletRepository;
    private final OperationLogRepository operationLogRepository;

    private final UserClient userClient;


    public WalletAccountServiceImpl( WalletRepository walletRepository, OperationLogRepository operationLogRepository, UserClient userClient) {

        this.walletRepository = walletRepository;
        this.operationLogRepository = operationLogRepository;
        this.userClient = userClient;
    }

    // =================================================
    // BALANCE
    // =================================================
    @Override
    @Transactional(readOnly = true)
    public BalanceDTO getBalance(String userId) {

        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new AccountNotFoundException("Wallet not found for userId: " + userId));

        BalanceDTO dto = new BalanceDTO();
        dto.setUserId(wallet.getUserId());
        dto.setBalance(wallet.getBalance());
        dto.setUpdatedAt(wallet.getUpdatedAt());
        return dto;
    }

    // =================================================
    // CREDIT
    // =================================================
    @Override
    public OperationResultDTO credit(String userId, BigDecimal amount, String requestId) {

        validateAmount(amount);

        // Idempotence
        if (requestId != null) {
            Optional<OperationLog> existing = operationLogRepository.findByRequestId(requestId);
            if (existing.isPresent()) {
                return toOperationResult(existing.get());
            }
        }

        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseGet(() -> createWallet(userId));

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);

//        OperationLog log = persistLog(requestId, "CREDIT", userId, amount, "OK", null);
//        return toOperationResult(log, wallet);
        OperationResultDTO dto = new OperationResultDTO();
        dto.setUserId(userId);
        dto.setCode("OK");
        dto.setSuccess(true);
        dto.setNewBalance(wallet.getBalance());
        dto.setProcessedAt(Instant.now());
        return dto;

    }

    // =================================================
    // DEBIT
    // =================================================
    @Override
    public OperationResultDTO debit(String userId, BigDecimal amount, String requestId) {

        validateAmount(amount);

        if (requestId != null) {
            Optional<OperationLog> existing = operationLogRepository.findByRequestId(requestId);
            if (existing.isPresent()) {
                return toOperationResult(existing.get());
            }
        }

        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new AccountNotFoundException("Wallet not found for userId: " + userId));

//        if (wallet.getBalance().compareTo(amount) < 0) {
//            OperationLog log = persistLog(
//                requestId,
//                "DEBIT",
//                userId,
//                amount,
//                "INSUFFICIENT_FUNDS",
//                "Not enough balance"
//            );
//            return toOperationResult(log, wallet);
//        }


        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Fonds_Insuffisant");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);


//        OperationLog log = persistLog(requestId, "DEBIT", userId, amount, "OK", null);
//        return toOperationResult(log, wallet)

        OperationResultDTO dto = new OperationResultDTO();
        dto.setUserId(userId);
        dto.setCode("OK");
        dto.setSuccess(true);
        dto.setNewBalance(wallet.getBalance());
        dto.setProcessedAt(Instant.now());
        return dto;

    }

    // =================================================
    // HELPERS
    // =================================================

    private Wallet createWallet(String userId) {
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);

//        // Le phone viendra plus tard du User Service
//        wallet.setPhone(null);

        // Phone récupéré depuis User Service (si dispo)
        wallet.setPhone(resolvePhone(userId));

        wallet.setBalance(BigDecimal.ZERO);
        wallet.setStatus(WalletStatus.ACTIVE);
        wallet.setCreatedAt(Instant.now());
        wallet.setUpdatedAt(Instant.now());

        return walletRepository.save(wallet);
    }


//    private OperationLog persistLog(
//        String requestId,
//        String type,
//        String userId,
//        BigDecimal amount,
//        String code,
//        String details
//    ) {
//        OperationLog log = new OperationLog();
//        log.setRequestId(requestId != null ? requestId : UUID.randomUUID().toString());
//        log.setOperationType(type);
//        log.setUserId(userId);
//        log.setAmount(amount);
//        log.setResultCode(code);
//        log.setDetails(details);
//        log.setProcessedAt(Instant.now());
//        return operationLogRepository.save(log);
//    }

//    private OperationResultDTO toOperationResult(OperationLog log, Wallet wallet) {
//        OperationResultDTO dto = toOperationResult(log);
//        dto.setNewBalance(wallet.getBalance());
//        return dto;
//    }

    private OperationResultDTO toOperationResult(OperationLog log) {
        OperationResultDTO dto = new OperationResultDTO();
        dto.setRequestId(log.getRequestId());
        dto.setUserId(log.getUserId());
        dto.setCode(log.getResultCode());
        dto.setMessage(log.getResultCode());
        dto.setSuccess("OK".equals(log.getResultCode()));
        dto.setProcessedAt(log.getProcessedAt());
        return dto;
    }

    private String resolvePhone(String userId) {
        try {
            UserDTO user = userClient.getUser(userId);
            return user != null ? user.getPhone() : null;
        } catch (Exception e) {
            log.warn("User service injoignable pour userId={}", userId);
            return null;
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("\n" + "Le montant doit être supérieur à zéro");
        }
    }

}
