package sn.ondmoney.txe.service.dto;

import sn.ondmoney.txe.domain.enumeration.WalletStatus;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for wallet balance response.
 */
public class WalletBalanceResponse {

    private String walletId;
    private String phoneNumber;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String currency;
    private WalletStatus status;
    private Instant lastUpdated;

    public WalletBalanceResponse() {}

    private WalletBalanceResponse(Builder builder) {
        this.walletId = builder.walletId;
        this.phoneNumber = builder.phoneNumber;
        this.balance = builder.balance;
        this.availableBalance = builder.availableBalance;
        this.currency = builder.currency;
        this.status = builder.status;
        this.lastUpdated = builder.lastUpdated;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getWalletId() { return walletId; }
    public String getPhoneNumber() { return phoneNumber; }
    public BigDecimal getBalance() { return balance; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public String getCurrency() { return currency; }
    public WalletStatus getStatus() { return status; }
    public Instant getLastUpdated() { return lastUpdated; }

    // Setters
    public void setWalletId(String walletId) { this.walletId = walletId; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setStatus(WalletStatus status) { this.status = status; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }

    public static class Builder {
        private String walletId;
        private String phoneNumber;
        private BigDecimal balance;
        private BigDecimal availableBalance;
        private String currency;
        private WalletStatus status;
        private Instant lastUpdated;

        public Builder walletId(String walletId) {
            this.walletId = walletId;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public Builder availableBalance(BigDecimal availableBalance) {
            this.availableBalance = availableBalance;
            return this;
        }

        public Builder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public Builder status(WalletStatus status) {
            this.status = status;
            return this;
        }

        public Builder lastUpdated(Instant lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public WalletBalanceResponse build() {
            return new WalletBalanceResponse(this);
        }
    }
}
