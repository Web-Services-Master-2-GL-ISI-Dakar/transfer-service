package sn.ondmoney.txe.service.dto;

import sn.ondmoney.txe.domain.enumeration.TransactionStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for P2P transfer response.
 */
public class P2PTransferResponse {

    private String transactionId;
    private TransactionStatus status;
    private BigDecimal amount;
    private BigDecimal fees;
    private BigDecimal totalDebited;
    private String senderPhone;
    private String receiverPhone;
    private String currency;
    private BigDecimal newBalance;
    private String description;
    private Instant initiatedAt;
    private Instant completedAt;
    private String message;

    public P2PTransferResponse() {}

    private P2PTransferResponse(Builder builder) {
        this.transactionId = builder.transactionId;
        this.status = builder.status;
        this.amount = builder.amount;
        this.fees = builder.fees;
        this.totalDebited = builder.totalDebited;
        this.senderPhone = builder.senderPhone;
        this.receiverPhone = builder.receiverPhone;
        this.currency = builder.currency;
        this.newBalance = builder.newBalance;
        this.description = builder.description;
        this.initiatedAt = builder.initiatedAt;
        this.completedAt = builder.completedAt;
        this.message = builder.message;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public TransactionStatus getStatus() { return status; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getFees() { return fees; }
    public BigDecimal getTotalDebited() { return totalDebited; }
    public String getSenderPhone() { return senderPhone; }
    public String getReceiverPhone() { return receiverPhone; }
    public String getCurrency() { return currency; }
    public BigDecimal getNewBalance() { return newBalance; }
    public String getDescription() { return description; }
    public Instant getInitiatedAt() { return initiatedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getMessage() { return message; }

    // Setters
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setFees(BigDecimal fees) { this.fees = fees; }
    public void setTotalDebited(BigDecimal totalDebited) { this.totalDebited = totalDebited; }
    public void setSenderPhone(String senderPhone) { this.senderPhone = senderPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setNewBalance(BigDecimal newBalance) { this.newBalance = newBalance; }
    public void setDescription(String description) { this.description = description; }
    public void setInitiatedAt(Instant initiatedAt) { this.initiatedAt = initiatedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
    public void setMessage(String message) { this.message = message; }

    public static class Builder {
        private String transactionId;
        private TransactionStatus status;
        private BigDecimal amount;
        private BigDecimal fees;
        private BigDecimal totalDebited;
        private String senderPhone;
        private String receiverPhone;
        private String currency;
        private BigDecimal newBalance;
        private String description;
        private Instant initiatedAt;
        private Instant completedAt;
        private String message;

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder fees(BigDecimal fees) {
            this.fees = fees;
            return this;
        }

        public Builder totalDebited(BigDecimal totalDebited) {
            this.totalDebited = totalDebited;
            return this;
        }

        public Builder senderPhone(String senderPhone) {
            this.senderPhone = senderPhone;
            return this;
        }

        public Builder receiverPhone(String receiverPhone) {
            this.receiverPhone = receiverPhone;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder newBalance(BigDecimal newBalance) {
            this.newBalance = newBalance;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder initiatedAt(Instant initiatedAt) {
            this.initiatedAt = initiatedAt;
            return this;
        }

        public Builder completedAt(Instant completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public P2PTransferResponse build() {
            return new P2PTransferResponse(this);
        }
    }
}
