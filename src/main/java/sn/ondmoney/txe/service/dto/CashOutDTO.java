package sn.ondmoney.txe.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import sn.ondmoney.txe.domain.enumeration.CashOutStatus;

/**
 * A DTO for the {@link sn.ondmoney.txe.domain.CashOut} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CashOutDTO implements Serializable {

    private Long id;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long senderId;

    private Long receiverId;

    @NotNull
    private CashOutStatus status;

    @NotNull
    private Instant date;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public CashOutStatus getStatus() {
        return status;
    }

    public void setStatus(CashOutStatus status) {
        this.status = status;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CashOutDTO)) {
            return false;
        }

        CashOutDTO cashOutDTO = (CashOutDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cashOutDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CashOutDTO{" +
            "id=" + getId() +
            ", amount=" + getAmount() +
            ", senderId=" + getSenderId() +
            ", receiverId=" + getReceiverId() +
            ", status='" + getStatus() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
