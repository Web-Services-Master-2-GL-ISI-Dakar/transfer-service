package sn.ondmoney.txe.service.impl;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.domain.enumeration.TransactionStatus;
import sn.ondmoney.txe.repository.TransferRepository;
import sn.ondmoney.txe.service.TransferBusinessService;
import sn.ondmoney.txe.service.api.AccountService;
import sn.ondmoney.txe.service.dto.TransferRequestDTO;
import sn.ondmoney.txe.service.dto.OperationResultDTO;
import sn.ondmoney.txe.service.exception.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class TransferBusinessServiceImpl implements TransferBusinessService {

    private final TransferRepository transferRepository;
    private final AccountService accountService;

    public TransferBusinessServiceImpl(TransferRepository transferRepository, AccountService accountService){
        this.transferRepository = transferRepository;
        this.accountService = accountService;
    }

    @Override
    @Transactional
    public Transfer executeP2P(TransferRequestDTO request) {
        if (request.getSenderUserId() == null || request.getReceiverUserId() == null || request.getAmount() == null) {
            throw new TransferException("Invalid request");
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Amount must be > 0");
        }
        Transfer tx = new Transfer();
        tx.setTxId(request.getRequestId() != null ? request.getRequestId() : UUID.randomUUID().toString());
        tx.setExternalTxId(request.getExternalRef());
        tx.setStatus(TransactionStatus.PENDING);
        tx.setAmount(request.getAmount());
        tx.setFees(BigDecimal.ZERO);
        tx.setSenderPhone(null);
        tx.setReceiverPhone(null);
        tx.setInitiatedAt(Instant.now());
        tx = transferRepository.save(tx);

        String reqId = request.getRequestId() != null ? request.getRequestId() : tx.getTxId();

        try {
            OperationResultDTO debit = accountService.debit(request.getSenderUserId(), request.getAmount(), reqId);
            if (!debit.isSuccess()) {
                tx.setStatus(TransactionStatus.FAILED);
                tx.setFailedAt(Instant.now());
                tx.setErrorMessage(debit.getMessage());
                transferRepository.save(tx);
                throw new InsufficientFundsException("Debit failed: " + debit.getMessage());
            }
            OperationResultDTO credit = accountService.credit(request.getReceiverUserId(), request.getAmount(), reqId + "-credit");
            if (!credit.isSuccess()) {
                accountService.credit(request.getSenderUserId(), request.getAmount(), reqId + "-compensate");
                tx.setStatus(TransactionStatus.FAILED);
                tx.setFailedAt(Instant.now());
                tx.setErrorMessage(credit.getMessage());
                transferRepository.save(tx);
                throw new TransferException("Credit failed: " + credit.getMessage());
            }
            tx.setStatus(TransactionStatus.COMPLETED);
            tx.setCompletedAt(Instant.now());
            transferRepository.save(tx);
            return tx;
        } catch (RuntimeException ex) {
            tx.setStatus(TransactionStatus.FAILED);
            tx.setFailedAt(Instant.now());
            tx.setErrorMessage(ex.getMessage());
            transferRepository.save(tx);
            throw ex;
        }
    }
}
