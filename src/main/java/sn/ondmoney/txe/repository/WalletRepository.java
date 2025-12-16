package sn.ondmoney.txe.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import sn.ondmoney.txe.domain.Wallet;

import java.util.Optional;

/**
 * Spring Data JPA repository for the Wallet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByUserId(String userId);

    Optional<Wallet> findByPhone(String phone);
}
