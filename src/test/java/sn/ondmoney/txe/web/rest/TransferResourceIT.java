package sn.ondmoney.txe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.txe.domain.TransferAsserts.*;
import static sn.ondmoney.txe.web.rest.TestUtil.createUpdateProxyForBean;
import static sn.ondmoney.txe.web.rest.TestUtil.sameNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import sn.ondmoney.txe.IntegrationTest;
import sn.ondmoney.txe.domain.Transfer;
import sn.ondmoney.txe.domain.enumeration.TransactionStatus;
import sn.ondmoney.txe.repository.TransferRepository;
import sn.ondmoney.txe.service.dto.TransferDTO;
import sn.ondmoney.txe.service.mapper.TransferMapper;

/**
 * Integration tests for the {@link TransferResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransferResourceIT {

    private static final String DEFAULT_TX_ID = "AAAAAAAAAA";
    private static final String UPDATED_TX_ID = "BBBBBBBBBB";

    private static final String DEFAULT_EXTERNAL_TX_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_TX_ID = "BBBBBBBBBB";

    private static final TransactionStatus DEFAULT_STATUS = TransactionStatus.PENDING;
    private static final TransactionStatus UPDATED_STATUS = TransactionStatus.COMPLETED;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(1);

    private static final BigDecimal DEFAULT_FEES = new BigDecimal(0);
    private static final BigDecimal UPDATED_FEES = new BigDecimal(1);

    private static final String DEFAULT_SENDER_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_SENDER_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_RECEIVER_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_RECEIVER_PHONE = "BBBBBBBBBB";

    private static final Instant DEFAULT_INITIATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INITIATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FAILED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FAILED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transfers";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransferRepository transferRepository;

    @Autowired
    private TransferMapper transferMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransferMockMvc;

    private Transfer transfer;

    private Transfer insertedTransfer;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transfer createEntity() {
        return new Transfer()
            .txId(DEFAULT_TX_ID)
            .externalTxId(DEFAULT_EXTERNAL_TX_ID)
            .status(DEFAULT_STATUS)
            .amount(DEFAULT_AMOUNT)
            .fees(DEFAULT_FEES)
            .senderPhone(DEFAULT_SENDER_PHONE)
            .receiverPhone(DEFAULT_RECEIVER_PHONE)
            .initiatedAt(DEFAULT_INITIATED_AT)
            .completedAt(DEFAULT_COMPLETED_AT)
            .failedAt(DEFAULT_FAILED_AT)
            .errorMessage(DEFAULT_ERROR_MESSAGE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transfer createUpdatedEntity() {
        return new Transfer()
            .txId(UPDATED_TX_ID)
            .externalTxId(UPDATED_EXTERNAL_TX_ID)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .fees(UPDATED_FEES)
            .senderPhone(UPDATED_SENDER_PHONE)
            .receiverPhone(UPDATED_RECEIVER_PHONE)
            .initiatedAt(UPDATED_INITIATED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .errorMessage(UPDATED_ERROR_MESSAGE);
    }

    @BeforeEach
    void initTest() {
        transfer = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTransfer != null) {
            transferRepository.delete(insertedTransfer);
            insertedTransfer = null;
        }
    }

    @Test
    @Transactional
    void createTransfer() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Transfer
        TransferDTO transferDTO = transferMapper.toDto(transfer);
        var returnedTransferDTO = om.readValue(
            restTransferMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransferDTO.class
        );

        // Validate the Transfer in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransfer = transferMapper.toEntity(returnedTransferDTO);
        assertTransferUpdatableFieldsEquals(returnedTransfer, getPersistedTransfer(returnedTransfer));

        insertedTransfer = returnedTransfer;
    }

    @Test
    @Transactional
    void createTransferWithExistingId() throws Exception {
        // Create the Transfer with an existing ID
        transfer.setId(1L);
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransferMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Transfer in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTxIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfer.setTxId(null);

        // Create the Transfer, which fails.
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        restTransferMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfer.setStatus(null);

        // Create the Transfer, which fails.
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        restTransferMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfer.setAmount(null);

        // Create the Transfer, which fails.
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        restTransferMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSenderPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfer.setSenderPhone(null);

        // Create the Transfer, which fails.
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        restTransferMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReceiverPhoneIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfer.setReceiverPhone(null);

        // Create the Transfer, which fails.
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        restTransferMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInitiatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transfer.setInitiatedAt(null);

        // Create the Transfer, which fails.
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        restTransferMockMvc
            .perform(post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransfers() throws Exception {
        // Initialize the database
        insertedTransfer = transferRepository.saveAndFlush(transfer);

        // Get all the transferList
        restTransferMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transfer.getId().intValue())))
            .andExpect(jsonPath("$.[*].txId").value(hasItem(DEFAULT_TX_ID)))
            .andExpect(jsonPath("$.[*].externalTxId").value(hasItem(DEFAULT_EXTERNAL_TX_ID)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].fees").value(hasItem(sameNumber(DEFAULT_FEES))))
            .andExpect(jsonPath("$.[*].senderPhone").value(hasItem(DEFAULT_SENDER_PHONE)))
            .andExpect(jsonPath("$.[*].receiverPhone").value(hasItem(DEFAULT_RECEIVER_PHONE)))
            .andExpect(jsonPath("$.[*].initiatedAt").value(hasItem(DEFAULT_INITIATED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].failedAt").value(hasItem(DEFAULT_FAILED_AT.toString())))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));
    }

    @Test
    @Transactional
    void getTransfer() throws Exception {
        // Initialize the database
        insertedTransfer = transferRepository.saveAndFlush(transfer);

        // Get the transfer
        restTransferMockMvc
            .perform(get(ENTITY_API_URL_ID, transfer.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transfer.getId().intValue()))
            .andExpect(jsonPath("$.txId").value(DEFAULT_TX_ID))
            .andExpect(jsonPath("$.externalTxId").value(DEFAULT_EXTERNAL_TX_ID))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.fees").value(sameNumber(DEFAULT_FEES)))
            .andExpect(jsonPath("$.senderPhone").value(DEFAULT_SENDER_PHONE))
            .andExpect(jsonPath("$.receiverPhone").value(DEFAULT_RECEIVER_PHONE))
            .andExpect(jsonPath("$.initiatedAt").value(DEFAULT_INITIATED_AT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()))
            .andExpect(jsonPath("$.failedAt").value(DEFAULT_FAILED_AT.toString()))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingTransfer() throws Exception {
        // Get the transfer
        restTransferMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransfer() throws Exception {
        // Initialize the database
        insertedTransfer = transferRepository.saveAndFlush(transfer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transfer
        Transfer updatedTransfer = transferRepository.findById(transfer.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransfer are not directly saved in db
        em.detach(updatedTransfer);
        updatedTransfer
            .txId(UPDATED_TX_ID)
            .externalTxId(UPDATED_EXTERNAL_TX_ID)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .fees(UPDATED_FEES)
            .senderPhone(UPDATED_SENDER_PHONE)
            .receiverPhone(UPDATED_RECEIVER_PHONE)
            .initiatedAt(UPDATED_INITIATED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .errorMessage(UPDATED_ERROR_MESSAGE);
        TransferDTO transferDTO = transferMapper.toDto(updatedTransfer);

        restTransferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transferDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transferDTO))
            )
            .andExpect(status().isOk());

        // Validate the Transfer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransferToMatchAllProperties(updatedTransfer);
    }

    @Test
    @Transactional
    void putNonExistingTransfer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfer.setId(longCount.incrementAndGet());

        // Create the Transfer
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transferDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transfer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransfer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfer.setId(longCount.incrementAndGet());

        // Create the Transfer
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transfer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransfer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfer.setId(longCount.incrementAndGet());

        // Create the Transfer
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transferDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transfer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransferWithPatch() throws Exception {
        // Initialize the database
        insertedTransfer = transferRepository.saveAndFlush(transfer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transfer using partial update
        Transfer partialUpdatedTransfer = new Transfer();
        partialUpdatedTransfer.setId(transfer.getId());

        partialUpdatedTransfer
            .status(UPDATED_STATUS)
            .senderPhone(UPDATED_SENDER_PHONE)
            .initiatedAt(UPDATED_INITIATED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .errorMessage(UPDATED_ERROR_MESSAGE);

        restTransferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransfer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransfer))
            )
            .andExpect(status().isOk());

        // Validate the Transfer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransferUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTransfer, transfer), getPersistedTransfer(transfer));
    }

    @Test
    @Transactional
    void fullUpdateTransferWithPatch() throws Exception {
        // Initialize the database
        insertedTransfer = transferRepository.saveAndFlush(transfer);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transfer using partial update
        Transfer partialUpdatedTransfer = new Transfer();
        partialUpdatedTransfer.setId(transfer.getId());

        partialUpdatedTransfer
            .txId(UPDATED_TX_ID)
            .externalTxId(UPDATED_EXTERNAL_TX_ID)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .fees(UPDATED_FEES)
            .senderPhone(UPDATED_SENDER_PHONE)
            .receiverPhone(UPDATED_RECEIVER_PHONE)
            .initiatedAt(UPDATED_INITIATED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .errorMessage(UPDATED_ERROR_MESSAGE);

        restTransferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransfer.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransfer))
            )
            .andExpect(status().isOk());

        // Validate the Transfer in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransferUpdatableFieldsEquals(partialUpdatedTransfer, getPersistedTransfer(partialUpdatedTransfer));
    }

    @Test
    @Transactional
    void patchNonExistingTransfer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfer.setId(longCount.incrementAndGet());

        // Create the Transfer
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transferDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transfer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransfer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfer.setId(longCount.incrementAndGet());

        // Create the Transfer
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transferDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transfer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransfer() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transfer.setId(longCount.incrementAndGet());

        // Create the Transfer
        TransferDTO transferDTO = transferMapper.toDto(transfer);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransferMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(transferDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transfer in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransfer() throws Exception {
        // Initialize the database
        insertedTransfer = transferRepository.saveAndFlush(transfer);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the transfer
        restTransferMockMvc
            .perform(delete(ENTITY_API_URL_ID, transfer.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return transferRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Transfer getPersistedTransfer(Transfer transfer) {
        return transferRepository.findById(transfer.getId()).orElseThrow();
    }

    protected void assertPersistedTransferToMatchAllProperties(Transfer expectedTransfer) {
        assertTransferAllPropertiesEquals(expectedTransfer, getPersistedTransfer(expectedTransfer));
    }

    protected void assertPersistedTransferToMatchUpdatableProperties(Transfer expectedTransfer) {
        assertTransferAllUpdatablePropertiesEquals(expectedTransfer, getPersistedTransfer(expectedTransfer));
    }
}
