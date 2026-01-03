package sn.ondmoney.txe.service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

/**
 * DTO for P2P transfer request.
 */
public class P2PTransferRequest {

    @NotBlank(message = "Le numéro de téléphone du destinataire est requis")
    @Pattern(regexp = "^\\+?[0-9]{9,15}$", message = "Format de numéro de téléphone invalide")
    private String receiverPhone;

    @NotNull(message = "Le montant est requis")
    @DecimalMin(value = "1", message = "Le montant minimum est de 1")
    private BigDecimal amount;

    private String description;

    private String pin;

    public P2PTransferRequest() {}

    public P2PTransferRequest(String receiverPhone, BigDecimal amount, String description, String pin) {
        this.receiverPhone = receiverPhone;
        this.amount = amount;
        this.description = description;
        this.pin = pin;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @Override
    public String toString() {
        return "P2PTransferRequest{" +
            "receiverPhone='" + maskPhone(receiverPhone) + '\'' +
            ", amount=" + amount +
            ", description='" + description + '\'' +
            '}';
    }

    private String maskPhone(String phone) {
        if (phone == null || phone.length() < 4) return "****";
        return "****" + phone.substring(phone.length() - 4);
    }
}
