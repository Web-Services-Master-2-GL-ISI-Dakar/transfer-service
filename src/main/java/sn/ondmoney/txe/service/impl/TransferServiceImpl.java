package sn.ondmoney.txe.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.repository.TransferRepository;
import sn.ondmoney.txe.service.TransferService;
import sn.ondmoney.txe.service.dto.TransferDTO;
import sn.ondmoney.txe.service.mapper.TransferMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.txe.domain.Transfer}.
 */
@Service
@Transactional
public class TransferServiceImpl implements TransferService {

    private static final Logger LOG = LoggerFactory.getLogger(TransferServiceImpl.class);

    private final TransferRepository transferRepository;

    private final TransferMapper transferMapper;

    public TransferServiceImpl(TransferRepository transferRepository, TransferMapper transferMapper) {
        this.transferRepository = transferRepository;
        this.transferMapper = transferMapper;
    }

    @Override
    public TransferDTO save(TransferDTO transferDTO) {
        LOG.debug("Request to save Transfer : {}", transferDTO);
        Transfer transfer = transferMapper.toEntity(transferDTO);
        transfer = transferRepository.save(transfer);
        return transferMapper.toDto(transfer);
    }

    @Override
    public TransferDTO update(TransferDTO transferDTO) {
        LOG.debug("Request to update Transfer : {}", transferDTO);
        Transfer transfer = transferMapper.toEntity(transferDTO);
        transfer = transferRepository.save(transfer);
        return transferMapper.toDto(transfer);
    }

    @Override
    public Optional<TransferDTO> partialUpdate(TransferDTO transferDTO) {
        LOG.debug("Request to partially update Transfer : {}", transferDTO);

        return transferRepository
            .findById(transferDTO.getId())
            .map(existingTransfer -> {
                transferMapper.partialUpdate(existingTransfer, transferDTO);

                return existingTransfer;
            })
            .map(transferRepository::save)
            .map(transferMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransferDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Transfers");
        return transferRepository.findAll(pageable).map(transferMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransferDTO> findOne(Long id) {
        LOG.debug("Request to get Transfer : {}", id);
        return transferRepository.findById(id).map(transferMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Transfer : {}", id);
        transferRepository.deleteById(id);
    }
}
