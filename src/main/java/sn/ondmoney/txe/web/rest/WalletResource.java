package sn.ondmoney.txe.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
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
import sn.ondmoney.txe.repository.WalletRepository;
import sn.ondmoney.txe.service.WalletService;
import sn.ondmoney.txe.service.api.AccountService;
import sn.ondmoney.txe.service.dto.BalanceDTO;
import sn.ondmoney.txe.service.dto.OperationResultDTO;
import sn.ondmoney.txe.service.dto.WalletDTO;
import sn.ondmoney.txe.service.dto.WalletOperationRequestDTO;
import sn.ondmoney.txe.web.rest.errors.BadRequestAlertException;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link sn.ondmoney.txe.domain.Wallet}.
 */
@RestController
@RequestMapping("/api/wallets")
public class WalletResource {

    private static final Logger LOG = LoggerFactory.getLogger(WalletResource.class);

    private static final String ENTITY_NAME = "ondmoneyTransactionEngineWallet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WalletService walletService;

    private final WalletRepository walletRepository;
    private final AccountService accountService;

    public WalletResource(WalletService walletService, WalletRepository walletRepository, AccountService accountService) {
        this.walletService = walletService;
        this.walletRepository = walletRepository;
        this.accountService = accountService;
    }


    /**
     * {@code POST  /wallets} : Create a new wallet.
     *
     * @param walletDTO the walletDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new walletDTO, or with status {@code 400 (Bad Request)} if the wallet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<WalletDTO> createWallet(@Valid @RequestBody WalletDTO walletDTO) throws URISyntaxException {
        LOG.debug("REST request to save Wallet : {}", walletDTO);
        if (walletDTO.getId() != null) {
            throw new BadRequestAlertException("A new wallet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        walletDTO = walletService.save(walletDTO);
        return ResponseEntity.created(new URI("/api/wallets/" + walletDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, walletDTO.getId().toString()))
            .body(walletDTO);
    }

    /**
     * {@code PUT  /wallets/:id} : Updates an existing wallet.
     *
     * @param id the id of the walletDTO to save.
     * @param walletDTO the walletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated walletDTO,
     * or with status {@code 400 (Bad Request)} if the walletDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the walletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<WalletDTO> updateWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WalletDTO walletDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Wallet : {}, {}", id, walletDTO);
        if (walletDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, walletDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!walletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        walletDTO = walletService.update(walletDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, walletDTO.getId().toString()))
            .body(walletDTO);
    }

    /**
     * {@code PATCH  /wallets/:id} : Partial updates given fields of an existing wallet, field will ignore if it is null
     *
     * @param id the id of the walletDTO to save.
     * @param walletDTO the walletDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated walletDTO,
     * or with status {@code 400 (Bad Request)} if the walletDTO is not valid,
     * or with status {@code 404 (Not Found)} if the walletDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the walletDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WalletDTO> partialUpdateWallet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WalletDTO walletDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Wallet partially : {}, {}", id, walletDTO);
        if (walletDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, walletDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!walletRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WalletDTO> result = walletService.partialUpdate(walletDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, walletDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /wallets} : get all the wallets.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wallets in body.
     */
    @GetMapping("")
    public ResponseEntity<List<WalletDTO>> getAllWallets(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Wallets");
        Page<WalletDTO> page = walletService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /wallets/:id} : get the "id" wallet.
     *
     * @param id the id of the walletDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the walletDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<WalletDTO> getWallet(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Wallet : {}", id);
        Optional<WalletDTO> walletDTO = walletService.findOne(id);
        return ResponseUtil.wrapOrNotFound(walletDTO);
    }

    /**
     * {@code DELETE  /wallets/:id} : delete the "id" wallet.
     *
     * @param id the id of the walletDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWallet(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Wallet : {}", id);
        walletService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }


//    @GetMapping("/{userId}/balance")
//    public BalanceDTO getBalance(@PathVariable String userId){ return accountService.getBalance(userId); }

    @GetMapping("/users/{userId}/balance")
    public BalanceDTO getBalance(@PathVariable String userId) {
        return accountService.getBalance(userId);
    }


    @PostMapping("/debit")
    public ResponseEntity<OperationResultDTO> debit(
        @Valid @RequestBody WalletOperationRequestDTO request,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        OperationResultDTO result = accountService.debit(
            request.getUserId(),
            request.getAmount(),
            idempotencyKey
        );
        return ResponseEntity.ok(result);
    }


    @PostMapping("/credit")
    public ResponseEntity<OperationResultDTO> credit(
        @Valid @RequestBody WalletOperationRequestDTO request,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        OperationResultDTO result = accountService.credit(
            request.getUserId(),
            request.getAmount(),
            idempotencyKey
        );
        return ResponseEntity.ok(result);
    }


}
