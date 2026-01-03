package sn.ondmoney.txe.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.repository.ProcessedEventRepository;
import sn.ondmoney.txe.repository.WalletRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for UserRegisteredConsumer.
 * Verifies wallet creation when receiving user.registered events.
 */
@SpringBootTest
@ActiveProfiles("testdev")
@EmbeddedKafka(
    partitions = 1,
    topics = {"user.registered", "wallet.created"},
    brokerProperties = {
        "listeners=PLAINTEXT://localhost:9092",
        "port=9092"
    }
)
@Transactional
class UserRegisteredConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ProcessedEventRepository processedEventRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String USER_REGISTERED_TOPIC = "user.registered";
    private static final String TEST_USER_ID = "usr_test123";
    private static final String TEST_PHONE = "+221771234567";

    @BeforeEach
    void setUp() {
        walletRepository.deleteAll();
        processedEventRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create wallet when receiving user.registered event")
    void handleUserRegistered_ShouldCreateWallet() throws Exception {
        // Given
        String eventId = "evt_" + UUID.randomUUID();
        Map<String, Object> userData = Map.of(
            "userId", TEST_USER_ID,
            "keycloakId", "kc_123",
            "phoneNumber", TEST_PHONE,
            "firstName", "John",
            "lastName", "Doe",
            "email", "john@example.com",
            "registeredAt", Instant.now().toString()
        );

        Map<String, Object> cloudEvent = Map.of(
            "specversion", "1.0",
            "id", eventId,
            "source", "ond-money/auth-service",
            "type", "user.registered",
            "datacontenttype", "application/json",
            "time", Instant.now().toString(),
            "subject", TEST_USER_ID,
            "data", userData,
            "ondmoney", Map.of(
                "correlationId", "corr_123",
                "version", "1.0.0",
                "environment", "development"
            )
        );

        String payload = objectMapper.writeValueAsString(cloudEvent);

        // When
        kafkaTemplate.send(USER_REGISTERED_TOPIC, TEST_USER_ID, payload);

        // Then - wait for wallet to be created
        await()
            .atMost(Duration.ofSeconds(10))
            .until(() -> walletRepository.findByUserId(TEST_USER_ID).isPresent());

        Optional<Wallet> wallet = walletRepository.findByUserId(TEST_USER_ID);
        assertThat(wallet).isPresent();
        assertThat(wallet.get().getPhone()).isEqualTo(TEST_PHONE);
        assertThat(wallet.get().getCurrency()).isEqualTo("XOF");
        assertThat(wallet.get().getBalance()).isNotNull();
    }

    @Test
    @DisplayName("Should be idempotent - not create duplicate wallet for same event")
    void handleUserRegistered_ShouldBeIdempotent() throws Exception {
        // Given
        String eventId = "evt_" + UUID.randomUUID();
        Map<String, Object> userData = Map.of(
            "userId", TEST_USER_ID,
            "keycloakId", "kc_123",
            "phoneNumber", TEST_PHONE,
            "firstName", "John",
            "lastName", "Doe"
        );

        Map<String, Object> cloudEvent = Map.of(
            "specversion", "1.0",
            "id", eventId,
            "source", "ond-money/auth-service",
            "type", "user.registered",
            "data", userData,
            "ondmoney", Map.of("correlationId", "corr_123")
        );

        String payload = objectMapper.writeValueAsString(cloudEvent);

        // When - send the same event twice
        kafkaTemplate.send(USER_REGISTERED_TOPIC, TEST_USER_ID, payload);
        kafkaTemplate.send(USER_REGISTERED_TOPIC, TEST_USER_ID, payload);

        // Then - only one wallet should be created
        await()
            .atMost(Duration.ofSeconds(10))
            .until(() -> walletRepository.findByUserId(TEST_USER_ID).isPresent());

        // Wait a bit more to ensure second message is processed
        Thread.sleep(2000);

        long walletCount = walletRepository.findAll().stream()
            .filter(w -> TEST_USER_ID.equals(w.getUserId()))
            .count();
        
        assertThat(walletCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Should not create wallet if user already has one")
    void handleUserRegistered_ShouldNotCreateDuplicateWallet() throws Exception {
        // Given - existing wallet
        Wallet existingWallet = new Wallet();
        existingWallet.setUserId(TEST_USER_ID);
        existingWallet.setPhone(TEST_PHONE);
        existingWallet.setCurrency("XOF");
        existingWallet.setBalance(java.math.BigDecimal.ZERO);
        existingWallet.setStatus(sn.ondmoney.txe.domain.enumeration.WalletStatus.ACTIVE);
        existingWallet.setVersion(1);
        existingWallet.setCreatedAt(Instant.now());
        walletRepository.save(existingWallet);

        String eventId = "evt_" + UUID.randomUUID();
        Map<String, Object> cloudEvent = Map.of(
            "specversion", "1.0",
            "id", eventId,
            "source", "ond-money/auth-service",
            "type", "user.registered",
            "data", Map.of(
                "userId", TEST_USER_ID,
                "phoneNumber", TEST_PHONE
            ),
            "ondmoney", Map.of("correlationId", "corr_123")
        );

        String payload = objectMapper.writeValueAsString(cloudEvent);

        // When
        kafkaTemplate.send(USER_REGISTERED_TOPIC, TEST_USER_ID, payload);

        // Then - still only one wallet
        Thread.sleep(3000); // Wait for processing

        long walletCount = walletRepository.findAll().stream()
            .filter(w -> TEST_USER_ID.equals(w.getUserId()))
            .count();
        
        assertThat(walletCount).isEqualTo(1);
    }
}
