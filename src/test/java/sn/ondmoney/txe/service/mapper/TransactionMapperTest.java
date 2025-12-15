package sn.ondmoney.txe.service.mapper;

import static sn.ondmoney.txe.domain.TransactionAsserts.*;
import static sn.ondmoney.txe.domain.TransactionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransactionMapperTest {

    private TransactionMapper transactionMapper;

    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTransactionSample1();
        var actual = transactionMapper.toEntity(transactionMapper.toDto(expected));
        assertTransactionAllPropertiesEquals(expected, actual);
    }
}
