package sn.ondmoney.txe.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import sn.ondmoney.txe.repository.CashOutRepository;
import sn.ondmoney.txe.service.CashOutService;
import sn.ondmoney.txe.service.dto.CashOutDTO;
import sn.ondmoney.txe.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.ondmoney.txe.domain.CashOut}.
 */
@RestController
@RequestMapping("/api/cash-outs")
public class CashOutResource {

    private static final Logger LOG = LoggerFactory.getLogger(CashOutResource.class);

    private static final String ENTITY_NAME = "ondmoneyTransactionEngineCashOut";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CashOutService cashOutService;

    private final CashOutRepository cashOutRepository;

    public CashOutResource(CashOutService cashOutService, CashOutRepository cashOutRepository) {
        this.cashOutService = cashOutService;
        this.cashOutRepository = cashOutRepository;
    }

    /**
     * {@code POST  /cash-outs} : Create a new cashOut.
     *
     * @param cashOutDTO the cashOutDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cashOutDTO, or with status {@code 400 (Bad Request)} if the cashOut has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CashOutDTO> createCashOut(@Valid @RequestBody CashOutDTO cashOutDTO) throws URISyntaxException {
        LOG.debug("REST request to save CashOut : {}", cashOutDTO);
        if (cashOutDTO.getId() != null) {
            throw new BadRequestAlertException("A new cashOut cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cashOutDTO = cashOutService.save(cashOutDTO);
        return ResponseEntity.created(new URI("/api/cash-outs/" + cashOutDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cashOutDTO.getId().toString()))
            .body(cashOutDTO);
    }

    /**
     * {@code PUT  /cash-outs/:id} : Updates an existing cashOut.
     *
     * @param id the id of the cashOutDTO to save.
     * @param cashOutDTO the cashOutDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashOutDTO,
     * or with status {@code 400 (Bad Request)} if the cashOutDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cashOutDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CashOutDTO> updateCashOut(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CashOutDTO cashOutDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CashOut : {}, {}", id, cashOutDTO);
        if (cashOutDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashOutDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashOutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cashOutDTO = cashOutService.update(cashOutDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashOutDTO.getId().toString()))
            .body(cashOutDTO);
    }

    /**
     * {@code PATCH  /cash-outs/:id} : Partial updates given fields of an existing cashOut, field will ignore if it is null
     *
     * @param id the id of the cashOutDTO to save.
     * @param cashOutDTO the cashOutDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cashOutDTO,
     * or with status {@code 400 (Bad Request)} if the cashOutDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cashOutDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cashOutDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CashOutDTO> partialUpdateCashOut(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CashOutDTO cashOutDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CashOut partially : {}, {}", id, cashOutDTO);
        if (cashOutDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cashOutDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cashOutRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CashOutDTO> result = cashOutService.partialUpdate(cashOutDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cashOutDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cash-outs} : get all the cashOuts.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cashOuts in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CashOutDTO>> getAllCashOuts(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CashOuts");
        Page<CashOutDTO> page = cashOutService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cash-outs/:id} : get the "id" cashOut.
     *
     * @param id the id of the cashOutDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cashOutDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CashOutDTO> getCashOut(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CashOut : {}", id);
        Optional<CashOutDTO> cashOutDTO = cashOutService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cashOutDTO);
    }

    /**
     * {@code DELETE  /cash-outs/:id} : delete the "id" cashOut.
     *
     * @param id the id of the cashOutDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCashOut(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CashOut : {}", id);
        cashOutService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
