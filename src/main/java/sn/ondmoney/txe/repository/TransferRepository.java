package sn.ondmoney.txe.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.ondmoney.txe.domain.Transfer;

/**
 * Spring Data JPA repository for the Transfer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {}
