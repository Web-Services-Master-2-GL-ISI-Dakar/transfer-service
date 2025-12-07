package sn.ondmoney.txe.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.ondmoney.txe.domain.enumeration.TransactionStatus;

/**
 * A Transfer.
 */
@Entity
@Table(name = "transfer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transfer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tx_id", nullable = false, unique = true)
    private String txId;

    @Column(name = "external_tx_id")
    private String externalTxId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @DecimalMin(value = "0")
    @Column(name = "fees", precision = 21, scale = 2)
    private BigDecimal fees;

    @NotNull
    @Column(name = "sender_phone", nullable = false)
    private String senderPhone;

    @NotNull
    @Column(name = "receiver_phone", nullable = false)
    private String receiverPhone;

    @NotNull
    @Column(name = "initiated_at", nullable = false)
    private Instant initiatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "failed_at")
    private Instant failedAt;

    @Size(max = 255)
    @Column(name = "error_message", length = 255)
    private String errorMessage;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transfer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxId() {
        return this.txId;
    }

    public Transfer txId(String txId) {
        this.setTxId(txId);
        return this;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getExternalTxId() {
        return this.externalTxId;
    }

    public Transfer externalTxId(String externalTxId) {
        this.setExternalTxId(externalTxId);
        return this;
    }

    public void setExternalTxId(String externalTxId) {
        this.externalTxId = externalTxId;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

    public Transfer status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Transfer amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFees() {
        return this.fees;
    }

    public Transfer fees(BigDecimal fees) {
        this.setFees(fees);
        return this;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public String getSenderPhone() {
        return this.senderPhone;
    }

    public Transfer senderPhone(String senderPhone) {
        this.setSenderPhone(senderPhone);
        return this;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return this.receiverPhone;
    }

    public Transfer receiverPhone(String receiverPhone) {
        this.setReceiverPhone(receiverPhone);
        return this;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public Instant getInitiatedAt() {
        return this.initiatedAt;
    }

    public Transfer initiatedAt(Instant initiatedAt) {
        this.setInitiatedAt(initiatedAt);
        return this;
    }

    public void setInitiatedAt(Instant initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public Transfer completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getFailedAt() {
        return this.failedAt;
    }

    public Transfer failedAt(Instant failedAt) {
        this.setFailedAt(failedAt);
        return this;
    }

    public void setFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Transfer errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transfer)) {
            return false;
        }
        return getId() != null && getId().equals(((Transfer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transfer{" +
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
