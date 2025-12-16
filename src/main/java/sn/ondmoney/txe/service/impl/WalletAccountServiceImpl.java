package sn.ondmoney.txe.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sn.ondmoney.txe.domain.OperationLog;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.repository.OperationLogRepository;
import sn.ondmoney.txe.repository.WalletRepository;
import sn.ondmoney.txe.service.api.AccountService;
import sn.ondmoney.txe.service.dto.BalanceDTO;
import sn.ondmoney.txe.service.dto.OperationResultDTO;
import sn.ondmoney.txe.service.exception.AccountNotFoundException;
import sn.ondmoney.txe.service.exception.InsufficientFundsException;

@Service
public class WalletAccountServiceImpl implements AccountService {

    private final Logger log = LoggerFactory.getLogger(WalletAccountServiceImpl.class);

    private final WalletRepository walletRepository;
    private final OperationLogRepository operationLogRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final String TOPIC = "transaction-events";

    public WalletAccountServiceImpl(
        WalletRepository walletRepository,
        OperationLogRepository operationLogRepository,
        KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.walletRepository = walletRepository;
        this.operationLogRepository = operationLogRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // -------------------------------------------------
    // GET BALANCE
    // -------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public BalanceDTO getBalance(String userId) {

        Wallet wallet = walletRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AccountNotFoundException("Wallet not found for userId: " + userId));

        BalanceDTO dto = new BalanceDTO();
        dto.setUserId(wallet.getUserId());
        dto.setBalance(wallet.getBalance());
        dto.setUpdatedAt(wallet.getUpdatedAt());
        return dto;
    }

    // -------------------------------------------------
    // DEBIT
    // -------------------------------------------------
    @Override
    @Transactional
    public OperationResultDTO debit(String userId, BigDecimal amount, String requestId) {

        validateAmount(amount);

        // Idempotency check
        if (requestId != null) {
            Optional<OperationLog> existing = operationLogRepository.findByRequestId(requestId);
            if (existing.isPresent()) {
                return toOperationResult(existing.get());
            }
        }

        Wallet wallet = walletRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AccountNotFoundException("Wallet not found for userId: " + userId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            OperationLog logEntry =
                persistLog(requestId, "DEBIT", userId, amount, "INSUFFICIENT_FUNDS", "Not enough balance");
            return toOperationResult(logEntry);
        }

        // Apply debit
        wallet.setBalance(wallet.getBalance().subtract(amount));
        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);

        OperationLog logEntry = persistLog(requestId, "DEBIT", userId, amount, "OK", null);

        publishEvent(userId, "DEBIT", amount, logEntry.getRequestId());

        return toOperationResult(logEntry, wallet);
    }

    // -------------------------------------------------
    // CREDIT
    // -------------------------------------------------
    @Override
    @Transactional
    public OperationResultDTO credit(String userId, BigDecimal amount, String requestId) {

        validateAmount(amount);

        // Idempotency
        if (requestId != null) {
            Optional<OperationLog> existing = operationLogRepository.findByRequestId(requestId);
            if (existing.isPresent()) {
                return toOperationResult(existing.get());
            }
        }

        // Wallet must exist ? -> JDL = wallet unique, mais on peut bootstrap un wallet vide.
        Wallet wallet = walletRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AccountNotFoundException("Wallet not found for userId: " + userId));

        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.setUpdatedAt(Instant.now());
        walletRepository.save(wallet);

        OperationLog logEntry = persistLog(requestId, "CREDIT", userId, amount, "OK", null);

        publishEvent(userId, "CREDIT", amount, logEntry.getRequestId());

        return toOperationResult(logEntry, wallet);
    }

    // -------------------------------------------------
    // Helpers
    // -------------------------------------------------
    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be > 0");
        }
    }

    private OperationLog persistLog(
        String requestId,
        String opType,
        String userId,
        BigDecimal amount,
        String resultCode,
        String details
    ) {
        OperationLog log = new OperationLog();
        log.setRequestId(requestId != null ? requestId : UUID.randomUUID().toString());
        log.setOperationType(opType);
        log.setUserId(userId);
        log.setAmount(amount);
        log.setResultCode(resultCode);
        log.setDetails(details);
        log.setProcessedAt(Instant.now());
        return operationLogRepository.save(log);
    }

    private void publishEvent(String userId, String opType, BigDecimal amount, String requestId) {
        if (kafkaTemplate == null) return;
        try {
            var payload = new java.util.HashMap<String, Object>();
            payload.put("userId", userId);
            payload.put("operation", opType);
            payload.put("amount", amount);
            payload.put("requestId", requestId);
            payload.put("timestamp", Instant.now());

            kafkaTemplate.send(TOPIC, payload);
        } catch (Exception e) {
            log.warn("Kafka publish failed: {}", e.getMessage());
        }
    }

    private OperationResultDTO toOperationResult(OperationLog op) {
        OperationResultDTO dto = new OperationResultDTO();
        dto.setRequestId(op.getRequestId());
        dto.setUserId(op.getUserId());
        dto.setCode(op.getResultCode());
        dto.setMessage(op.getResultCode());
        dto.setSuccess("OK".equals(op.getResultCode()));
        dto.setProcessedAt(op.getProcessedAt());
        dto.setNewBalance(null); // pas de wallet
        return dto;
    }

    private OperationResultDTO toOperationResult(OperationLog op, Wallet wallet) {
        OperationResultDTO dto = toOperationResult(op);
        dto.setNewBalance(wallet.getBalance());
        return dto;
    }
}
