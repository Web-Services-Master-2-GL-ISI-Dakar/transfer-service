package sn.ondmoney.txe.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

/**
 * Event payload for user registration - mirrors AuthMS event.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisteredEvent {

    private String userId;
    private String keycloakId;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String email;
    private Instant registeredAt;

    public UserRegisteredEvent() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeycloakId() {
        return keycloakId;
    }

    public void setKeycloakId(String keycloakId) {
        this.keycloakId = keycloakId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }
}
