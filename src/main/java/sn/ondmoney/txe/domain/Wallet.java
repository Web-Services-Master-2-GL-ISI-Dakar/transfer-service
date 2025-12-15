package sn.ondmoney.txe.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.ondmoney.txe.domain.enumeration.WalletStatus;

/**
 * A Wallet.
 */
@Entity
@Table(name = "wallet")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @NotNull
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private WalletStatus status;

    @NotNull
    @Column(name = "balance", precision = 21, scale = 2, nullable = false)
    private BigDecimal balance;

    @NotNull
    @Column(name = "version", nullable = false)
    private Integer version;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "debitedAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "debitedAccount", "creditedAccount" }, allowSetters = true)
    private Set<Transaction> debits = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "creditedAccount")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "debitedAccount", "creditedAccount" }, allowSetters = true)
    private Set<Transaction> credits = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sender")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sender", "receiver" }, allowSetters = true)
    private Set<Transfer> transfersSents = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "receiver")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "sender", "receiver" }, allowSetters = true)
    private Set<Transfer> transfersReceiveds = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Wallet id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return this.userId;
    }

    public Wallet userId(String userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return this.phone;
    }

    public Wallet phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public WalletStatus getStatus() {
        return this.status;
    }

    public Wallet status(WalletStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(WalletStatus status) {
        this.status = status;
    }

    public BigDecimal getBalance() {
        return this.balance;
    }

    public Wallet balance(BigDecimal balance) {
        this.setBalance(balance);
        return this;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Wallet version(Integer version) {
        this.setVersion(version);
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Wallet createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Wallet updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Transaction> getDebits() {
        return this.debits;
    }

    public void setDebits(Set<Transaction> transactions) {
        if (this.debits != null) {
            this.debits.forEach(i -> i.setDebitedAccount(null));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.setDebitedAccount(this));
        }
        this.debits = transactions;
    }

    public Wallet debits(Set<Transaction> transactions) {
        this.setDebits(transactions);
        return this;
    }

    public Wallet addDebits(Transaction transaction) {
        this.debits.add(transaction);
        transaction.setDebitedAccount(this);
        return this;
    }

    public Wallet removeDebits(Transaction transaction) {
        this.debits.remove(transaction);
        transaction.setDebitedAccount(null);
        return this;
    }

    public Set<Transaction> getCredits() {
        return this.credits;
    }

    public void setCredits(Set<Transaction> transactions) {
        if (this.credits != null) {
            this.credits.forEach(i -> i.setCreditedAccount(null));
        }
        if (transactions != null) {
            transactions.forEach(i -> i.setCreditedAccount(this));
        }
        this.credits = transactions;
    }

    public Wallet credits(Set<Transaction> transactions) {
        this.setCredits(transactions);
        return this;
    }

    public Wallet addCredits(Transaction transaction) {
        this.credits.add(transaction);
        transaction.setCreditedAccount(this);
        return this;
    }

    public Wallet removeCredits(Transaction transaction) {
        this.credits.remove(transaction);
        transaction.setCreditedAccount(null);
        return this;
    }

    public Set<Transfer> getTransfersSents() {
        return this.transfersSents;
    }

    public void setTransfersSents(Set<Transfer> transfers) {
        if (this.transfersSents != null) {
            this.transfersSents.forEach(i -> i.setSender(null));
        }
        if (transfers != null) {
            transfers.forEach(i -> i.setSender(this));
        }
        this.transfersSents = transfers;
    }

    public Wallet transfersSents(Set<Transfer> transfers) {
        this.setTransfersSents(transfers);
        return this;
    }

    public Wallet addTransfersSent(Transfer transfer) {
        this.transfersSents.add(transfer);
        transfer.setSender(this);
        return this;
    }

    public Wallet removeTransfersSent(Transfer transfer) {
        this.transfersSents.remove(transfer);
        transfer.setSender(null);
        return this;
    }

    public Set<Transfer> getTransfersReceiveds() {
        return this.transfersReceiveds;
    }

    public void setTransfersReceiveds(Set<Transfer> transfers) {
        if (this.transfersReceiveds != null) {
            this.transfersReceiveds.forEach(i -> i.setReceiver(null));
        }
        if (transfers != null) {
            transfers.forEach(i -> i.setReceiver(this));
        }
        this.transfersReceiveds = transfers;
    }

    public Wallet transfersReceiveds(Set<Transfer> transfers) {
        this.setTransfersReceiveds(transfers);
        return this;
    }

    public Wallet addTransfersReceived(Transfer transfer) {
        this.transfersReceiveds.add(transfer);
        transfer.setReceiver(this);
        return this;
    }

    public Wallet removeTransfersReceived(Transfer transfer) {
        this.transfersReceiveds.remove(transfer);
        transfer.setReceiver(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Wallet)) {
            return false;
        }
        return getId() != null && getId().equals(((Wallet) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Wallet{" +
            "id=" + getId() +
            ", userId='" + getUserId() + "'" +
            ", phone='" + getPhone() + "'" +
            ", status='" + getStatus() + "'" +
            ", balance=" + getBalance() +
            ", version=" + getVersion() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            "}";
    }
}
