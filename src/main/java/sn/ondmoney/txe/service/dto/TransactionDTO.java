package sn.ondmoney.txe.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import sn.ondmoney.txe.domain.enumeration.TransactionStatus;
import sn.ondmoney.txe.domain.enumeration.TransactionType;

/**
 * A DTO for the {@link sn.ondmoney.txe.domain.Transaction} entity.
 */
@Schema(description = "Transaction: Journal de tous les mouvements de solde.")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransactionDTO implements Serializable {

    private Long id;

    @NotNull
    private String txId;

    private String externalTxId;

    @NotNull
    private TransactionType type;

    @NotNull
    private TransactionStatus status;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @DecimalMin(value = "0")
    private BigDecimal fees;

    @NotNull
    private String source;

    @NotNull
    private String destination;

    @NotNull
    private Instant initiatedAt;

    private Instant completedAt;

    private Instant failedAt;

    @Size(max = 255)
    private String errorMessage;

    private WalletDTO debitedAccount;

    private WalletDTO creditedAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getExternalTxId() {
        return externalTxId;
    }

    public void setExternalTxId(String externalTxId) {
        this.externalTxId = externalTxId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFees() {
        return fees;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Instant getInitiatedAt() {
        return initiatedAt;
    }

    public void setInitiatedAt(Instant initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    public void setFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public WalletDTO getDebitedAccount() {
        return debitedAccount;
    }

    public void setDebitedAccount(WalletDTO debitedAccount) {
        this.debitedAccount = debitedAccount;
    }

    public WalletDTO getCreditedAccount() {
        return creditedAccount;
    }

    public void setCreditedAccount(WalletDTO creditedAccount) {
        this.creditedAccount = creditedAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransactionDTO)) {
            return false;
        }

        TransactionDTO transactionDTO = (TransactionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transactionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransactionDTO{" +
            "id=" + getId() +
            ", txId='" + getTxId() + "'" +
            ", externalTxId='" + getExternalTxId() + "'" +
            ", type='" + getType() + "'" +
            ", status='" + getStatus() + "'" +
            ", amount=" + getAmount() +
            ", fees=" + getFees() +
            ", source='" + getSource() + "'" +
            ", destination='" + getDestination() + "'" +
            ", initiatedAt='" + getInitiatedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", failedAt='" + getFailedAt() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            ", debitedAccount=" + getDebitedAccount() +
            ", creditedAccount=" + getCreditedAccount() +
            "}";
    }
}
