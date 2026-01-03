package sn.ondmoney.txe.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Event payload for wallet creation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletCreatedEvent {

    private String walletId;
    private String userId;
    private String currency;
    private BigDecimal initialBalance;
    private String status;
    private Instant createdAt;

    public WalletCreatedEvent() {}

    private WalletCreatedEvent(Builder builder) {
        this.walletId = builder.walletId;
        this.userId = builder.userId;
        this.currency = builder.currency;
        this.initialBalance = builder.initialBalance;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public static class Builder {
        private String walletId;
        private String userId;
        private String currency;
        private BigDecimal initialBalance;
        private String status;
        private Instant createdAt;

        public Builder walletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder initialBalance(BigDecimal initialBalance) {
            this.initialBalance = initialBalance;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public WalletCreatedEvent build() {
            return new WalletCreatedEvent(this);
        }
    }
}
