package sn.ondmoney.txe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.txe.domain.TransactionAsserts.*;
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
import sn.ondmoney.txe.domain.Transaction;
import sn.ondmoney.txe.domain.enumeration.TransactionStatus;
import sn.ondmoney.txe.domain.enumeration.TransactionType;
import sn.ondmoney.txe.repository.TransactionRepository;
import sn.ondmoney.txe.service.dto.TransactionDTO;
import sn.ondmoney.txe.service.mapper.TransactionMapper;

/**
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_TX_ID = "AAAAAAAAAA";
    private static final String UPDATED_TX_ID = "BBBBBBBBBB";

    private static final String DEFAULT_EXTERNAL_TX_ID = "AAAAAAAAAA";
    private static final String UPDATED_EXTERNAL_TX_ID = "BBBBBBBBBB";

    private static final TransactionType DEFAULT_TYPE = TransactionType.TRANSFER_P2P;
    private static final TransactionType UPDATED_TYPE = TransactionType.CARD_RECHARGE;

    private static final TransactionStatus DEFAULT_STATUS = TransactionStatus.PENDING;
    private static final TransactionStatus UPDATED_STATUS = TransactionStatus.COMPLETED;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(0);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(1);

    private static final BigDecimal DEFAULT_FEES = new BigDecimal(0);
    private static final BigDecimal UPDATED_FEES = new BigDecimal(1);

    private static final String DEFAULT_SOURCE = "AAAAAAAAAA";
    private static final String UPDATED_SOURCE = "BBBBBBBBBB";

    private static final String DEFAULT_DESTINATION = "AAAAAAAAAA";
    private static final String UPDATED_DESTINATION = "BBBBBBBBBB";

    private static final Instant DEFAULT_INITIATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_INITIATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_COMPLETED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_COMPLETED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_FAILED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_FAILED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ERROR_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_ERROR_MESSAGE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    private Transaction insertedTransaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity() {
        return new Transaction()
            .txId(DEFAULT_TX_ID)
            .externalTxId(DEFAULT_EXTERNAL_TX_ID)
            .type(DEFAULT_TYPE)
            .status(DEFAULT_STATUS)
            .amount(DEFAULT_AMOUNT)
            .fees(DEFAULT_FEES)
            .source(DEFAULT_SOURCE)
            .destination(DEFAULT_DESTINATION)
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
    public static Transaction createUpdatedEntity() {
        return new Transaction()
            .txId(UPDATED_TX_ID)
            .externalTxId(UPDATED_EXTERNAL_TX_ID)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .fees(UPDATED_FEES)
            .source(UPDATED_SOURCE)
            .destination(UPDATED_DESTINATION)
            .initiatedAt(UPDATED_INITIATED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .errorMessage(UPDATED_ERROR_MESSAGE);
    }

    @BeforeEach
    void initTest() {
        transaction = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTransaction != null) {
            transactionRepository.delete(insertedTransaction);
            insertedTransaction = null;
        }
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);
        var returnedTransactionDTO = om.readValue(
            restTransactionMockMvc
                .perform(
                    post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TransactionDTO.class
        );

        // Validate the Transaction in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTransaction = transactionMapper.toEntity(returnedTransactionDTO);
        assertTransactionUpdatableFieldsEquals(returnedTransaction, getPersistedTransaction(returnedTransaction));

        insertedTransaction = returnedTransaction;
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTxIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setTxId(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setType(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setStatus(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setAmount(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSourceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setSource(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDestinationIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setDestination(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkInitiatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        transaction.setInitiatedAt(null);

        // Create the Transaction, which fails.
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        restTransactionMockMvc
            .perform(
                post(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].txId").value(hasItem(DEFAULT_TX_ID)))
            .andExpect(jsonPath("$.[*].externalTxId").value(hasItem(DEFAULT_EXTERNAL_TX_ID)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].fees").value(hasItem(sameNumber(DEFAULT_FEES))))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE)))
            .andExpect(jsonPath("$.[*].destination").value(hasItem(DEFAULT_DESTINATION)))
            .andExpect(jsonPath("$.[*].initiatedAt").value(hasItem(DEFAULT_INITIATED_AT.toString())))
            .andExpect(jsonPath("$.[*].completedAt").value(hasItem(DEFAULT_COMPLETED_AT.toString())))
            .andExpect(jsonPath("$.[*].failedAt").value(hasItem(DEFAULT_FAILED_AT.toString())))
            .andExpect(jsonPath("$.[*].errorMessage").value(hasItem(DEFAULT_ERROR_MESSAGE)));
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.txId").value(DEFAULT_TX_ID))
            .andExpect(jsonPath("$.externalTxId").value(DEFAULT_EXTERNAL_TX_ID))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.fees").value(sameNumber(DEFAULT_FEES)))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE))
            .andExpect(jsonPath("$.destination").value(DEFAULT_DESTINATION))
            .andExpect(jsonPath("$.initiatedAt").value(DEFAULT_INITIATED_AT.toString()))
            .andExpect(jsonPath("$.completedAt").value(DEFAULT_COMPLETED_AT.toString()))
            .andExpect(jsonPath("$.failedAt").value(DEFAULT_FAILED_AT.toString()))
            .andExpect(jsonPath("$.errorMessage").value(DEFAULT_ERROR_MESSAGE));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction
            .txId(UPDATED_TX_ID)
            .externalTxId(UPDATED_EXTERNAL_TX_ID)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .fees(UPDATED_FEES)
            .source(UPDATED_SOURCE)
            .destination(UPDATED_DESTINATION)
            .initiatedAt(UPDATED_INITIATED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .errorMessage(UPDATED_ERROR_MESSAGE);
        TransactionDTO transactionDTO = transactionMapper.toDto(updatedTransaction);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTransactionToMatchAllProperties(updatedTransaction);
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(transactionDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .fees(UPDATED_FEES)
            .source(UPDATED_SOURCE)
            .destination(UPDATED_DESTINATION)
            .initiatedAt(UPDATED_INITIATED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .errorMessage(UPDATED_ERROR_MESSAGE);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTransaction, transaction),
            getPersistedTransaction(transaction)
        );
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .txId(UPDATED_TX_ID)
            .externalTxId(UPDATED_EXTERNAL_TX_ID)
            .type(UPDATED_TYPE)
            .status(UPDATED_STATUS)
            .amount(UPDATED_AMOUNT)
            .fees(UPDATED_FEES)
            .source(UPDATED_SOURCE)
            .destination(UPDATED_DESTINATION)
            .initiatedAt(UPDATED_INITIATED_AT)
            .completedAt(UPDATED_COMPLETED_AT)
            .failedAt(UPDATED_FAILED_AT)
            .errorMessage(UPDATED_ERROR_MESSAGE);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTransactionUpdatableFieldsEquals(partialUpdatedTransaction, getPersistedTransaction(partialUpdatedTransaction));
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        transaction.setId(longCount.incrementAndGet());

        // Create the Transaction
        TransactionDTO transactionDTO = transactionMapper.toDto(transaction);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).with(csrf()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(transactionDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        insertedTransaction = transactionRepository.saveAndFlush(transaction);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return transactionRepository.count();
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

    protected Transaction getPersistedTransaction(Transaction transaction) {
        return transactionRepository.findById(transaction.getId()).orElseThrow();
    }

    protected void assertPersistedTransactionToMatchAllProperties(Transaction expectedTransaction) {
        assertTransactionAllPropertiesEquals(expectedTransaction, getPersistedTransaction(expectedTransaction));
    }

    protected void assertPersistedTransactionToMatchUpdatableProperties(Transaction expectedTransaction) {
        assertTransactionAllUpdatablePropertiesEquals(expectedTransaction, getPersistedTransaction(expectedTransaction));
    }
}
