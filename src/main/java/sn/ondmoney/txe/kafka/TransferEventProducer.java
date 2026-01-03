package sn.ondmoney.txe.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.domain.Wallet;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Producer for transfer-related Kafka events.
 * Publishes events for P2P transfers to be consumed by transaction-history-service.
 */
@Service
public class TransferEventProducer {

    private static final Logger LOG = LoggerFactory.getLogger(TransferEventProducer.class);
    
    private static final String TRANSFER_INITIATED_TOPIC = "transfer.initiated";
    private static final String TRANSFER_COMPLETED_TOPIC = "transfer.completed";
    private static final String TRANSFER_FAILED_TOPIC = "transfer.failed";
    private static final String SOURCE = "ond-money/txe-service";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransferEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Publish transfer.initiated event when a transfer is started.
     */
    public void publishTransferInitiated(Transfer transfer, Wallet sender, Wallet receiver, String correlationId) {
        LOG.info("Publishing transfer.initiated event for transfer: {}", transfer.getTxId());

        try {
            Map<String, Object> eventData = buildTransferEventData(transfer, sender, receiver);
            eventData.put("initiatedAt", transfer.getInitiatedAt().toString());

            String cloudEvent = wrapInCloudEvent(
                TRANSFER_INITIATED_TOPIC,
                eventData,
                transfer.getTxId(),
                correlationId
            );

            kafkaTemplate.send(
                TRANSFER_INITIATED_TOPIC,
                transfer.getTxId(),
                cloudEvent
            ).get(5, TimeUnit.SECONDS);

            LOG.info("Successfully published transfer.initiated event for transfer: {}", transfer.getTxId());

        } catch (Exception e) {
            LOG.error("Failed to publish transfer.initiated event: {}", e.getMessage(), e);
            // Don't throw - we don't want to fail the transfer if event publishing fails
        }
    }

    /**
     * Publish transfer.completed event when a transfer succeeds.
     */
    public void publishTransferCompleted(
            Transfer transfer,
            Wallet sender,
            Wallet receiver,
            BigDecimal senderNewBalance,
            BigDecimal receiverNewBalance,
            String correlationId) {
        
        LOG.info("Publishing transfer.completed event for transfer: {}", transfer.getTxId());

        try {
            Map<String, Object> eventData = buildTransferEventData(transfer, sender, receiver);
            eventData.put("completedAt", transfer.getCompletedAt() != null ? 
                transfer.getCompletedAt().toString() : Instant.now().toString());
            eventData.put("senderNewBalance", senderNewBalance.toString());
            eventData.put("receiverNewBalance", receiverNewBalance.toString());
            eventData.put("senderName", buildFullName(sender));
            eventData.put("receiverName", buildFullName(receiver));

            String cloudEvent = wrapInCloudEvent(
                TRANSFER_COMPLETED_TOPIC,
                eventData,
                transfer.getTxId(),
                correlationId
            );

            kafkaTemplate.send(
                TRANSFER_COMPLETED_TOPIC,
                transfer.getTxId(),
                cloudEvent
            ).get(5, TimeUnit.SECONDS);

            LOG.info("Successfully published transfer.completed event for transfer: {}", transfer.getTxId());

        } catch (Exception e) {
            LOG.error("Failed to publish transfer.completed event: {}", e.getMessage(), e);
            // Don't throw - transfer already completed, just log the error
        }
    }

    /**
     * Publish transfer.failed event when a transfer fails.
     */
    public void publishTransferFailed(
            Transfer transfer,
            Wallet sender,
            Wallet receiver,
            String failureReason,
            String failureMessage,
            String correlationId) {
        
        LOG.info("Publishing transfer.failed event for transfer: {}", transfer.getTxId());

        try {
            Map<String, Object> eventData = buildTransferEventData(transfer, sender, receiver);
            eventData.put("failedAt", transfer.getFailedAt() != null ? 
                transfer.getFailedAt().toString() : Instant.now().toString());
            eventData.put("failureReason", failureReason);
            eventData.put("failureMessage", failureMessage);

            String cloudEvent = wrapInCloudEvent(
                TRANSFER_FAILED_TOPIC,
                eventData,
                transfer.getTxId(),
                correlationId
            );

            kafkaTemplate.send(
                TRANSFER_FAILED_TOPIC,
                transfer.getTxId(),
                cloudEvent
            ).get(5, TimeUnit.SECONDS);

            LOG.info("Successfully published transfer.failed event for transfer: {}", transfer.getTxId());

        } catch (Exception e) {
            LOG.error("Failed to publish transfer.failed event: {}", e.getMessage(), e);
        }
    }

    /**
     * Build common transfer event data.
     */
    private Map<String, Object> buildTransferEventData(Transfer transfer, Wallet sender, Wallet receiver) {
        Map<String, Object> data = new HashMap<>();
        data.put("transferId", transfer.getTxId());
        data.put("senderId", sender != null ? sender.getUserId() : null);
        data.put("receiverId", receiver != null ? receiver.getUserId() : null);
        data.put("senderPhoneNumber", transfer.getSenderPhone());
        data.put("receiverPhoneNumber", transfer.getReceiverPhone());
        data.put("amount", transfer.getAmount().toString());
        data.put("fees", transfer.getFees() != null ? transfer.getFees().toString() : "0");
        data.put("currency", sender != null ? sender.getCurrency() : "XOF");
        data.put("description", "Transfert P2P");
        data.put("status", transfer.getStatus().name());
        return data;
    }

    /**
     * Build full name from wallet (placeholder - wallet doesn't have name fields).
     */
    private String buildFullName(Wallet wallet) {
        if (wallet == null) return null;
        // Wallet doesn't have firstName/lastName, use phone as identifier
        return wallet.getPhone();
    }

    /**
     * Wrap event in CloudEvents envelope.
     */
    private String wrapInCloudEvent(String eventType, Map<String, Object> data, String subject, String correlationId)
            throws JsonProcessingException {
        String eventId = "evt_" + UUID.randomUUID().toString();

        Map<String, Object> cloudEvent = new HashMap<>();
        cloudEvent.put("specversion", "1.0");
        cloudEvent.put("id", eventId);
        cloudEvent.put("source", SOURCE);
        cloudEvent.put("type", eventType);
        cloudEvent.put("datacontenttype", "application/json");
        cloudEvent.put("time", Instant.now().toString());
        cloudEvent.put("subject", subject);
        cloudEvent.put("data", data);
        cloudEvent.put("ondmoney", Map.of(
            "correlationId", correlationId != null ? correlationId : eventId,
            "version", "1.0.0",
            "environment", getEnvironment()
        ));

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
