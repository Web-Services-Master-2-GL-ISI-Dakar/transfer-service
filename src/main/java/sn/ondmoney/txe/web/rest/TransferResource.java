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
import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.repository.TransferRepository;
import sn.ondmoney.txe.service.TransferBusinessService;
import sn.ondmoney.txe.service.TransferService;
import sn.ondmoney.txe.service.dto.TransferDTO;
import sn.ondmoney.txe.service.dto.TransferRequestDTO;
import sn.ondmoney.txe.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.ondmoney.txe.domain.Transfer}.
 */
@RestController
@RequestMapping("/api/transfers")
public class TransferResource {

    private static final Logger LOG = LoggerFactory.getLogger(TransferResource.class);

    private static final String ENTITY_NAME = "ondmoneyTransactionEngineTransfer";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransferService transferService;

    private final TransferRepository transferRepository;

    private final TransferBusinessService transferBusinessService;

    public TransferResource(TransferService transferService, TransferRepository transferRepository, TransferBusinessService transferBusinessService) {
        this.transferService = transferService;
        this.transferRepository = transferRepository;
        this.transferBusinessService = transferBusinessService;
    }


    @PostMapping("/p2p")
    public Transfer p2p(@RequestBody TransferRequestDTO req){
        return transferBusinessService.executeP2P(req);
    }

    /**
     * {@code POST  /transfers} : Create a new transfer.
     *
     * @param transferDTO the transferDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transferDTO, or with status {@code 400 (Bad Request)} if the transfer has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TransferDTO> createTransfer(@Valid @RequestBody TransferDTO transferDTO) throws URISyntaxException {
        LOG.debug("REST request to save Transfer : {}", transferDTO);
        if (transferDTO.getId() != null) {
            throw new BadRequestAlertException("A new transfer cannot already have an ID", ENTITY_NAME, "idexists");
        }
        transferDTO = transferService.save(transferDTO);
        return ResponseEntity.created(new URI("/api/transfers/" + transferDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, transferDTO.getId().toString()))
            .body(transferDTO);
    }

    /**
     * {@code PUT  /transfers/:id} : Updates an existing transfer.
     *
     * @param id the id of the transferDTO to save.
     * @param transferDTO the transferDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transferDTO,
     * or with status {@code 400 (Bad Request)} if the transferDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transferDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TransferDTO> updateTransfer(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TransferDTO transferDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Transfer : {}, {}", id, transferDTO);
        if (transferDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transferDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transferRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        transferDTO = transferService.update(transferDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transferDTO.getId().toString()))
            .body(transferDTO);
    }

    /**
     * {@code PATCH  /transfers/:id} : Partial updates given fields of an existing transfer, field will ignore if it is null
     *
     * @param id the id of the transferDTO to save.
     * @param transferDTO the transferDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transferDTO,
     * or with status {@code 400 (Bad Request)} if the transferDTO is not valid,
     * or with status {@code 404 (Not Found)} if the transferDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the transferDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransferDTO> partialUpdateTransfer(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TransferDTO transferDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Transfer partially : {}, {}", id, transferDTO);
        if (transferDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transferDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transferRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransferDTO> result = transferService.partialUpdate(transferDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transferDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /transfers} : get all the transfers.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transfers in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TransferDTO>> getAllTransfers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Transfers");
        Page<TransferDTO> page = transferService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transfers/:id} : get the "id" transfer.
     *
     * @param id the id of the transferDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transferDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransferDTO> getTransfer(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Transfer : {}", id);
        Optional<TransferDTO> transferDTO = transferService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transferDTO);
    }

    /**
     * {@code DELETE  /transfers/:id} : delete the "id" transfer.
     *
     * @param id the id of the transferDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransfer(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Transfer : {}", id);
        transferService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
