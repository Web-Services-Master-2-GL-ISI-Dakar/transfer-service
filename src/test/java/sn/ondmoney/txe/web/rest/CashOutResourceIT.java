package sn.ondmoney.txe.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sn.ondmoney.txe.domain.CashOutAsserts.*;
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
import sn.ondmoney.txe.domain.CashOut;
import sn.ondmoney.txe.domain.enumeration.CashOutStatus;
import sn.ondmoney.txe.repository.CashOutRepository;
import sn.ondmoney.txe.service.dto.CashOutDTO;
import sn.ondmoney.txe.service.mapper.CashOutMapper;

/**
 * Integration tests for the {@link CashOutResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CashOutResourceIT {

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final Long DEFAULT_SENDER_ID = 1L;
    private static final Long UPDATED_SENDER_ID = 2L;

    private static final Long DEFAULT_RECEIVER_ID = 1L;
    private static final Long UPDATED_RECEIVER_ID = 2L;

    private static final CashOutStatus DEFAULT_STATUS = CashOutStatus.PENDING;
    private static final CashOutStatus UPDATED_STATUS = CashOutStatus.SUCCESS;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/cash-outs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CashOutRepository cashOutRepository;

    @Autowired
    private CashOutMapper cashOutMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCashOutMockMvc;

    private CashOut cashOut;

    private CashOut insertedCashOut;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashOut createEntity() {
        return new CashOut()
            .amount(DEFAULT_AMOUNT)
            .senderId(DEFAULT_SENDER_ID)
            .receiverId(DEFAULT_RECEIVER_ID)
            .status(DEFAULT_STATUS)
            .date(DEFAULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CashOut createUpdatedEntity() {
        return new CashOut()
            .amount(UPDATED_AMOUNT)
            .senderId(UPDATED_SENDER_ID)
            .receiverId(UPDATED_RECEIVER_ID)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE);
    }

    @BeforeEach
    void initTest() {
        cashOut = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCashOut != null) {
            cashOutRepository.delete(insertedCashOut);
            insertedCashOut = null;
        }
    }

    @Test
    @Transactional
    void createCashOut() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CashOut
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);
        var returnedCashOutDTO = om.readValue(
            restCashOutMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CashOutDTO.class
        );

        // Validate the CashOut in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCashOut = cashOutMapper.toEntity(returnedCashOutDTO);
        assertCashOutUpdatableFieldsEquals(returnedCashOut, getPersistedCashOut(returnedCashOut));

        insertedCashOut = returnedCashOut;
    }

    @Test
    @Transactional
    void createCashOutWithExistingId() throws Exception {
        // Create the CashOut with an existing ID
        cashOut.setId(1L);
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCashOutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CashOut in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkAmountIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashOut.setAmount(null);

        // Create the CashOut, which fails.
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        restCashOutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSenderIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashOut.setSenderId(null);

        // Create the CashOut, which fails.
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        restCashOutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashOut.setStatus(null);

        // Create the CashOut, which fails.
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        restCashOutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cashOut.setDate(null);

        // Create the CashOut, which fails.
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        restCashOutMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCashOuts() throws Exception {
        // Initialize the database
        insertedCashOut = cashOutRepository.saveAndFlush(cashOut);

        // Get all the cashOutList
        restCashOutMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cashOut.getId().intValue())))
            .andExpect(jsonPath("$.[*].amount").value(hasItem(sameNumber(DEFAULT_AMOUNT))))
            .andExpect(jsonPath("$.[*].senderId").value(hasItem(DEFAULT_SENDER_ID.intValue())))
            .andExpect(jsonPath("$.[*].receiverId").value(hasItem(DEFAULT_RECEIVER_ID.intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getCashOut() throws Exception {
        // Initialize the database
        insertedCashOut = cashOutRepository.saveAndFlush(cashOut);

        // Get the cashOut
        restCashOutMockMvc
            .perform(get(ENTITY_API_URL_ID, cashOut.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cashOut.getId().intValue()))
            .andExpect(jsonPath("$.amount").value(sameNumber(DEFAULT_AMOUNT)))
            .andExpect(jsonPath("$.senderId").value(DEFAULT_SENDER_ID.intValue()))
            .andExpect(jsonPath("$.receiverId").value(DEFAULT_RECEIVER_ID.intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCashOut() throws Exception {
        // Get the cashOut
        restCashOutMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCashOut() throws Exception {
        // Initialize the database
        insertedCashOut = cashOutRepository.saveAndFlush(cashOut);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashOut
        CashOut updatedCashOut = cashOutRepository.findById(cashOut.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCashOut are not directly saved in db
        em.detach(updatedCashOut);
        updatedCashOut
            .amount(UPDATED_AMOUNT)
            .senderId(UPDATED_SENDER_ID)
            .receiverId(UPDATED_RECEIVER_ID)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE);
        CashOutDTO cashOutDTO = cashOutMapper.toDto(updatedCashOut);

        restCashOutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashOutDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO))
            )
            .andExpect(status().isOk());

        // Validate the CashOut in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCashOutToMatchAllProperties(updatedCashOut);
    }

    @Test
    @Transactional
    void putNonExistingCashOut() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashOut.setId(longCount.incrementAndGet());

        // Create the CashOut
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashOutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cashOutDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashOut in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCashOut() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashOut.setId(longCount.incrementAndGet());

        // Create the CashOut
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashOutMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cashOutDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashOut in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCashOut() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashOut.setId(longCount.incrementAndGet());

        // Create the CashOut
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashOutMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cashOutDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashOut in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCashOutWithPatch() throws Exception {
        // Initialize the database
        insertedCashOut = cashOutRepository.saveAndFlush(cashOut);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashOut using partial update
        CashOut partialUpdatedCashOut = new CashOut();
        partialUpdatedCashOut.setId(cashOut.getId());

        partialUpdatedCashOut.amount(UPDATED_AMOUNT).senderId(UPDATED_SENDER_ID).status(UPDATED_STATUS).date(UPDATED_DATE);

        restCashOutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashOut.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashOut))
            )
            .andExpect(status().isOk());

        // Validate the CashOut in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashOutUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCashOut, cashOut), getPersistedCashOut(cashOut));
    }

    @Test
    @Transactional
    void fullUpdateCashOutWithPatch() throws Exception {
        // Initialize the database
        insertedCashOut = cashOutRepository.saveAndFlush(cashOut);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cashOut using partial update
        CashOut partialUpdatedCashOut = new CashOut();
        partialUpdatedCashOut.setId(cashOut.getId());

        partialUpdatedCashOut
            .amount(UPDATED_AMOUNT)
            .senderId(UPDATED_SENDER_ID)
            .receiverId(UPDATED_RECEIVER_ID)
            .status(UPDATED_STATUS)
            .date(UPDATED_DATE);

        restCashOutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCashOut.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCashOut))
            )
            .andExpect(status().isOk());

        // Validate the CashOut in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCashOutUpdatableFieldsEquals(partialUpdatedCashOut, getPersistedCashOut(partialUpdatedCashOut));
    }

    @Test
    @Transactional
    void patchNonExistingCashOut() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashOut.setId(longCount.incrementAndGet());

        // Create the CashOut
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCashOutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cashOutDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashOutDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashOut in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCashOut() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashOut.setId(longCount.incrementAndGet());

        // Create the CashOut
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashOutMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cashOutDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CashOut in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCashOut() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cashOut.setId(longCount.incrementAndGet());

        // Create the CashOut
        CashOutDTO cashOutDTO = cashOutMapper.toDto(cashOut);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCashOutMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cashOutDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CashOut in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCashOut() throws Exception {
        // Initialize the database
        insertedCashOut = cashOutRepository.saveAndFlush(cashOut);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cashOut
        restCashOutMockMvc
            .perform(delete(ENTITY_API_URL_ID, cashOut.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cashOutRepository.count();
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

    protected CashOut getPersistedCashOut(CashOut cashOut) {
        return cashOutRepository.findById(cashOut.getId()).orElseThrow();
    }

    protected void assertPersistedCashOutToMatchAllProperties(CashOut expectedCashOut) {
        assertCashOutAllPropertiesEquals(expectedCashOut, getPersistedCashOut(expectedCashOut));
    }

    protected void assertPersistedCashOutToMatchUpdatableProperties(CashOut expectedCashOut) {
        assertCashOutAllUpdatablePropertiesEquals(expectedCashOut, getPersistedCashOut(expectedCashOut));
    }
}
