package sn.ondmoney.txe.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sn.ondmoney.txe.domain.Wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Producer for wallet-related Kafka events.
 */
@Service
public class WalletEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(WalletEventProducer.class);
    private static final String WALLET_CREATED_TOPIC = "wallet.created";
    private static final String SOURCE = "ond-money/txe-service";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public WalletEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Publish wallet.created event.
     */
    public void publishWalletCreated(Wallet wallet, String correlationId) {
        LOG.info("Publishing wallet.created event for wallet: {}", wallet.getId());

        try {
            WalletCreatedEvent eventData = WalletCreatedEvent.builder()
                .walletId("wal_" + wallet.getId())
                .userId(wallet.getUserId())
                .currency(wallet.getCurrency())
                .initialBalance(wallet.getBalance() != null ? wallet.getBalance() : BigDecimal.ZERO)
                .status(wallet.getStatus() != null ? wallet.getStatus().name() : "ACTIVE")
                .createdAt(wallet.getCreatedDate() != null ? wallet.getCreatedDate() : Instant.now())
                .build();

            String cloudEvent = wrapInCloudEvent(eventData, wallet.getUserId(), correlationId);

            kafkaTemplate.send(
                WALLET_CREATED_TOPIC,
                wallet.getUserId(),
                cloudEvent
            ).get(5, TimeUnit.SECONDS);

            LOG.info("Successfully published wallet.created event for wallet: {}", wallet.getId());

        } catch (Exception e) {
            LOG.error("Failed to publish wallet.created event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to publish wallet.created event", e);
        }
    }

    /**
     * Wrap event in CloudEvents envelope.
     */
    private String wrapInCloudEvent(WalletCreatedEvent event, String subject, String correlationId) 
            throws JsonProcessingException {
        String eventId = "evt_" + UUID.randomUUID().toString();

        Map<String, Object> cloudEvent = Map.of(
            "specversion", "1.0",
            "id", eventId,
            "source", SOURCE,
            "type", WALLET_CREATED_TOPIC,
            "datacontenttype", "application/json",
            "time", Instant.now().toString(),
            "subject", subject,
            "data", event,
            "ondmoney", Map.of(
                "correlationId", correlationId != null ? correlationId : eventId,
                "version", "1.0.0",
                "environment", getEnvironment()
            )
        );

        return objectMapper.writeValueAsString(cloudEvent);
    }

    private String getEnvironment() {
        String profile = System.getProperty("spring.profiles.active", "dev");
        return switch (profile) {
            case "prod" -> "production";
            case "staging" -> "staging";
            default -> "development";
        };
    }
}
