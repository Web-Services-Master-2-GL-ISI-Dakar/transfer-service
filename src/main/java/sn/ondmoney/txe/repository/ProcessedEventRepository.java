package sn.ondmoney.txe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sn.ondmoney.txe.domain.ProcessedEvent;

import java.time.Instant;

/**
 * Repository for ProcessedEvent - tracks processed Kafka events for idempotency.
 */
@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, String> {

    /**
     * Check if an event has already been processed.
     */
    boolean existsByEventId(String eventId);

    /**
     * Delete events processed before a given time.
     */
    void deleteByProcessedAtBefore(Instant before);
}
