package sn.ondmoney.txe.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.txe.domain.Wallet;
import sn.ondmoney.txe.repository.WalletRepository;
import sn.ondmoney.txe.service.WalletService;
import sn.ondmoney.txe.service.dto.WalletDTO;
import sn.ondmoney.txe.service.mapper.WalletMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.txe.domain.Wallet}.
 */
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final Logger LOG = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletRepository walletRepository;

    private final WalletMapper walletMapper;

    public WalletServiceImpl(WalletRepository walletRepository, WalletMapper walletMapper) {
        this.walletRepository = walletRepository;
        this.walletMapper = walletMapper;
    }

    @Override
    public WalletDTO save(WalletDTO walletDTO) {
        LOG.debug("Request to save Wallet : {}", walletDTO);
        Wallet wallet = walletMapper.toEntity(walletDTO);
        wallet = walletRepository.save(wallet);
        return walletMapper.toDto(wallet);
    }

    @Override
    public WalletDTO update(WalletDTO walletDTO) {
        LOG.debug("Request to update Wallet : {}", walletDTO);
        Wallet wallet = walletMapper.toEntity(walletDTO);
        wallet = walletRepository.save(wallet);
        return walletMapper.toDto(wallet);
    }

    @Override
    public Optional<WalletDTO> partialUpdate(WalletDTO walletDTO) {
        LOG.debug("Request to partially update Wallet : {}", walletDTO);

        return walletRepository
            .findById(walletDTO.getId())
            .map(existingWallet -> {
                walletMapper.partialUpdate(existingWallet, walletDTO);

                return existingWallet;
            })
            .map(walletRepository::save)
            .map(walletMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WalletDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Wallets");
        return walletRepository.findAll(pageable).map(walletMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WalletDTO> findOne(Long id) {
        LOG.debug("Request to get Wallet : {}", id);
        return walletRepository.findById(id).map(walletMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Wallet : {}", id);
        walletRepository.deleteById(id);
    }
}
