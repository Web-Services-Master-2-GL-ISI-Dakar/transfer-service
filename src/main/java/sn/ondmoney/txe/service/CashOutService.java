package sn.ondmoney.txe.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.txe.domain.CashOut;
import sn.ondmoney.txe.repository.CashOutRepository;
import sn.ondmoney.txe.service.dto.CashOutDTO;
import sn.ondmoney.txe.service.mapper.CashOutMapper;

/**
 * Service Implementation for managing {@link sn.ondmoney.txe.domain.CashOut}.
 */
@Service
@Transactional
public class CashOutService {

    private static final Logger LOG = LoggerFactory.getLogger(CashOutService.class);

    private final CashOutRepository cashOutRepository;

    private final CashOutMapper cashOutMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CashOutService(CashOutRepository cashOutRepository, CashOutMapper cashOutMapper, KafkaTemplate<String, Object> kafkaTemplate) {
        this.cashOutRepository = cashOutRepository;
        this.cashOutMapper = cashOutMapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Save a cashOut.
     *
     * @param cashOutDTO the entity to save.
     * @return the persisted entity.
     */
    public CashOutDTO save(CashOutDTO cashOutDTO) {
        LOG.debug("Request to save CashOut : {}", cashOutDTO);
        CashOut cashOut = cashOutMapper.toEntity(cashOutDTO);
        cashOut = cashOutRepository.save(cashOut);

        // Publier l'événement Kafka
        kafkaTemplate.send(TOPIC, cashOut);

        return cashOutMapper.toDto(cashOut);
    }

    /**
     * Update a cashOut.
     *
     * @param cashOutDTO the entity to save.
     * @return the persisted entity.
     */
    public CashOutDTO update(CashOutDTO cashOutDTO) {
        LOG.debug("Request to update CashOut : {}", cashOutDTO);
        CashOut cashOut = cashOutMapper.toEntity(cashOutDTO);
        cashOut = cashOutRepository.save(cashOut);

        kafkaTemplate.send(TOPIC, cashOut);

        return cashOutMapper.toDto(cashOut);
    }

    /**
     * Partially update a cashOut.
     *
     * @param cashOutDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CashOutDTO> partialUpdate(CashOutDTO cashOutDTO) {
        LOG.debug("Request to partially update CashOut : {}", cashOutDTO);

        return cashOutRepository
            .findById(cashOutDTO.getId())
            .map(existingCashOut -> {
                cashOutMapper.partialUpdate(existingCashOut, cashOutDTO);

                return existingCashOut;
            })
            .map(cashOutRepository::save)
            .map(saved -> {
                kafkaTemplate.send(TOPIC, saved);
                return cashOutMapper.toDto(saved);
            });
    }

    /**
     * Get all the cashOuts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CashOutDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CashOuts");
        return cashOutRepository.findAll(pageable).map(cashOutMapper::toDto);
    }

    /**
     * Get one cashOut by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CashOutDTO> findOne(Long id) {
        LOG.debug("Request to get CashOut : {}", id);
        return cashOutRepository.findById(id).map(cashOutMapper::toDto);
    }

    /**
     * Delete the cashOut by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CashOut : {}", id);
        cashOutRepository.deleteById(id);

        // Publier un événement de suppression si nécessaire
        kafkaTemplate.send(TOPIC, "Deleted CashOut ID: " + id);
    }
}
