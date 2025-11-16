package sn.ondmoney.txe.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import sn.ondmoney.txe.domain.enumeration.CashOutStatus;

/**
 * A CashOut.
 */
@Entity
@Table(name = "cash_out")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CashOut implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "amount", precision = 21, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CashOutStatus status;

    @NotNull
    @Column(name = "date", nullable = false)
    private Instant date;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CashOut id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public CashOut amount(BigDecimal amount) {
        this.setAmount(amount);
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getSenderId() {
        return this.senderId;
    }

    public CashOut senderId(Long senderId) {
        this.setSenderId(senderId);
        return this;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return this.receiverId;
    }

    public CashOut receiverId(Long receiverId) {
        this.setReceiverId(receiverId);
        return this;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public CashOutStatus getStatus() {
        return this.status;
    }

    public CashOut status(CashOutStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(CashOutStatus status) {
        this.status = status;
    }

    public Instant getDate() {
        return this.date;
    }

    public CashOut date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CashOut)) {
            return false;
        }
        return getId() != null && getId().equals(((CashOut) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CashOut{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", senderId=" + getSenderId() +
            ", receiverId=" + getReceiverId() +
            ", status='" + getStatus() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
