package sn.ondmoney.txe.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.txe.domain.ProcessedEvent;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.domain.enumeration.WalletStatus;
import sn.ondmoney.txe.repository.ProcessedEventRepository;
import sn.ondmoney.txe.repository.WalletRepository;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Kafka consumer for user.registered events.
 * Creates a wallet for newly registered users.
 */
@Component
public class UserRegisteredConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(UserRegisteredConsumer.class);
    private static final String TOPIC = "user.registered";
    private static final String DEFAULT_CURRENCY = "XOF";

    private final WalletRepository walletRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final WalletEventProducer walletEventProducer;
    private final ObjectMapper objectMapper;

    public UserRegisteredConsumer(
            WalletRepository walletRepository,
            ProcessedEventRepository processedEventRepository,
            WalletEventProducer walletEventProducer,
            ObjectMapper objectMapper) {
        this.walletRepository = walletRepository;
        this.processedEventRepository = processedEventRepository;
        this.walletEventProducer = walletEventProducer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = TOPIC,
        groupId = "txe-consumers",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void handleUserRegistered(
            @Payload String payload,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(value = "ce_id", required = false) String eventId) {

        LOG.info("Received user.registered event for key: {}", key);

        try {
            // Parse CloudEvents envelope
            JsonNode cloudEvent = objectMapper.readTree(payload);

            // Extract event ID from CloudEvents envelope if not in header
            if (eventId == null || eventId.isBlank()) {
                eventId = cloudEvent.has("id") ? cloudEvent.get("id").asText() : key + "-" + System.currentTimeMillis();
            }

            // Extract correlation ID
            String correlationId = eventId;
            if (cloudEvent.has("ondmoney") && cloudEvent.get("ondmoney").has("correlationId")) {
                correlationId = cloudEvent.get("ondmoney").get("correlationId").asText();
            }

            // Idempotency check
            if (processedEventRepository.existsByEventId(eventId)) {
                LOG.info("Event {} already processed, skipping", eventId);
                return;
            }

            // Extract data from CloudEvents envelope
            JsonNode dataNode = cloudEvent.get("data");
            if (dataNode == null) {
                LOG.error("No data found in CloudEvents envelope");
                return;
            }

            UserRegisteredEvent event = objectMapper.treeToValue(dataNode, UserRegisteredEvent.class);

            // Check if wallet already exists for user
            if (walletRepository.findByUserId(event.getUserId()).isPresent()) {
                LOG.info("Wallet already exists for user: {}", event.getUserId());
                processedEventRepository.save(new ProcessedEvent(eventId, TOPIC));
                return;
            }

            // Create wallet
            Wallet wallet = new Wallet();
            wallet.setUserId(event.getUserId());
            wallet.setKeycloakId(event.getKeycloakId());
            wallet.setPhoneNumber(event.getPhoneNumber());
            wallet.setCurrency(DEFAULT_CURRENCY);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setStatus(WalletStatus.ACTIVE);
            wallet.setCreatedDate(Instant.now());

            wallet = walletRepository.save(wallet);
            LOG.info("Wallet created successfully: {} for user: {}", wallet.getId(), event.getUserId());

            // Publish wallet.created event
            walletEventProducer.publishWalletCreated(wallet, correlationId);

            // Mark as processed
            processedEventRepository.save(new ProcessedEvent(eventId, TOPIC));

            LOG.info("Successfully processed user.registered event for user: {}", event.getUserId());

        } catch (Exception e) {
            LOG.error("Error processing user.registered event: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process user.registered event", e);
        }
    }
}
