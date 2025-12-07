package sn.ondmoney.txe.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import sn.ondmoney.txe.service.dto.WalletDTO;

/**
 * Service Interface for managing {@link sn.ondmoney.txe.domain.Wallet}.
 */
public interface WalletService {
    /**
     * Save a wallet.
     *
     * @param walletDTO the entity to save.
     * @return the persisted entity.
     */
    WalletDTO save(WalletDTO walletDTO);

    /**
     * Updates a wallet.
     *
     * @param walletDTO the entity to update.
     * @return the persisted entity.
     */
    WalletDTO update(WalletDTO walletDTO);

    /**
     * Partially updates a wallet.
     *
     * @param walletDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<WalletDTO> partialUpdate(WalletDTO walletDTO);

    /**
     * Get all the wallets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<WalletDTO> findAll(Pageable pageable);

    /**
     * Get the "id" wallet.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<WalletDTO> findOne(Long id);

    /**
     * Delete the "id" wallet.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
