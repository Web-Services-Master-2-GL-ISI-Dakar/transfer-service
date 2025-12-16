package sn.ondmoney.txe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.ondmoney.txe.domain.OperationLog;

import java.util.Optional;

public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {
    Optional<OperationLog> findByRequestId(String requestId);
}
