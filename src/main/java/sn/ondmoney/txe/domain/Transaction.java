package sn.ondmoney.txe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.ondmoney.txe.domain.enumeration.TransactionStatus;
import sn.ondmoney.txe.domain.enumeration.TransactionType;

/**
 * Transaction: Journal de tous les mouvements de solde.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Transaction implements Serializable {

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
    @Column(name = "type", nullable = false)
    private TransactionType type;

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
    @Column(name = "source", nullable = false)
    private String source;

    @NotNull
    @Column(name = "destination", nullable = false)
    private String destination;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "debits", "credits", "transfersSents", "transfersReceiveds" }, allowSetters = true)
    private Wallet debitedAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "debits", "credits", "transfersSents", "transfersReceiveds" }, allowSetters = true)
    private Wallet creditedAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTxId() {
        return this.txId;
    }

    public Transaction txId(String txId) {
        this.setTxId(txId);
        return this;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public String getExternalTxId() {
        return this.externalTxId;
    }

    public Transaction externalTxId(String externalTxId) {
        this.setExternalTxId(externalTxId);
        return this;
    }

    public void setExternalTxId(String externalTxId) {
        this.externalTxId = externalTxId;
    }

    public TransactionType getType() {
        return this.type;
    }

    public Transaction type(TransactionType type) {
        this.setType(type);
        return this;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

    public Transaction status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Transaction amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getFees() {
        return this.fees;
    }

    public Transaction fees(BigDecimal fees) {
        this.setFees(fees);
        return this;
    }

    public void setFees(BigDecimal fees) {
        this.fees = fees;
    }

    public String getSource() {
        return this.source;
    }

    public Transaction source(String source) {
        this.setSource(source);
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return this.destination;
    }

    public Transaction destination(String destination) {
        this.setDestination(destination);
        return this;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Instant getInitiatedAt() {
        return this.initiatedAt;
    }

    public Transaction initiatedAt(Instant initiatedAt) {
        this.setInitiatedAt(initiatedAt);
        return this;
    }

    public void setInitiatedAt(Instant initiatedAt) {
        this.initiatedAt = initiatedAt;
    }

    public Instant getCompletedAt() {
        return this.completedAt;
    }

    public Transaction completedAt(Instant completedAt) {
        this.setCompletedAt(completedAt);
        return this;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getFailedAt() {
        return this.failedAt;
    }

    public Transaction failedAt(Instant failedAt) {
        this.setFailedAt(failedAt);
        return this;
    }

    public void setFailedAt(Instant failedAt) {
        this.failedAt = failedAt;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Transaction errorMessage(String errorMessage) {
        this.setErrorMessage(errorMessage);
        return this;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Wallet getDebitedAccount() {
        return this.debitedAccount;
    }

    public void setDebitedAccount(Wallet wallet) {
        this.debitedAccount = wallet;
    }

    public Transaction debitedAccount(Wallet wallet) {
        this.setDebitedAccount(wallet);
        return this;
    }

    public Wallet getCreditedAccount() {
        return this.creditedAccount;
    }

    public void setCreditedAccount(Wallet wallet) {
        this.creditedAccount = wallet;
    }

    public Transaction creditedAccount(Wallet wallet) {
        this.setCreditedAccount(wallet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return getId() != null && getId().equals(((Transaction) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
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
            "}";
    }
}
