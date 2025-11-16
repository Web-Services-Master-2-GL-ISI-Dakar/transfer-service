package sn.ondmoney.txe.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.ondmoney.txe.domain.CashOut;

/**
 * Spring Data JPA repository for the CashOut entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CashOutRepository extends JpaRepository<CashOut, Long> {}
