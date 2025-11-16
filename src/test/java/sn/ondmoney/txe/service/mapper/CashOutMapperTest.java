package sn.ondmoney.txe.service.mapper;

import static sn.ondmoney.txe.domain.CashOutAsserts.*;
import static sn.ondmoney.txe.domain.CashOutTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CashOutMapperTest {

    private CashOutMapper cashOutMapper;

    @BeforeEach
    void setUp() {
        cashOutMapper = new CashOutMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCashOutSample1();
        var actual = cashOutMapper.toEntity(cashOutMapper.toDto(expected));
        assertCashOutAllPropertiesEquals(expected, actual);
    }
}
