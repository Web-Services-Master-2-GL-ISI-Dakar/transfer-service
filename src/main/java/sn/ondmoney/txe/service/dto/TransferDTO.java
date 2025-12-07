package sn.ondmoney.txe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import sn.ondmoney.txe.domain.enumeration.TransactionStatus;

/**
 * A DTO for the {@link sn.ondmoney.txe.domain.Transfer} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TransferDTO implements Serializable {

    private Long id;

    @NotNull
    private String txId;

    private String externalTxId;

    @NotNull
    private TransactionStatus status;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @DecimalMin(value = "0")
    private BigDecimal fees;

    @NotNull
    private String senderPhone;

    @NotNull
    private String receiverPhone;

    @NotNull
    private Instant initiatedAt;

    private Instant completedAt;

    private Instant failedAt;

    @Size(max = 255)
    private String errorMessage;

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

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TransferDTO)) {
            return false;
        }

        TransferDTO transferDTO = (TransferDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, transferDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TransferDTO{" +
            "id=" + getId() +
            ", txId='" + getTxId() + "'" +
            ", externalTxId='" + getExternalTxId() + "'" +
            ", status='" + getStatus() + "'" +
            ", amount=" + getAmount() +
            ", fees=" + getFees() +
            ", senderPhone='" + getSenderPhone() + "'" +
            ", receiverPhone='" + getReceiverPhone() + "'" +
            ", initiatedAt='" + getInitiatedAt() + "'" +
            ", completedAt='" + getCompletedAt() + "'" +
            ", failedAt='" + getFailedAt() + "'" +
            ", errorMessage='" + getErrorMessage() + "'" +
            "}";
    }
}
