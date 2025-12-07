package sn.ondmoney.txe.service.mapper;

import static sn.ondmoney.txe.domain.TransferAsserts.*;
import static sn.ondmoney.txe.domain.TransferTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TransferMapperTest {

    private TransferMapper transferMapper;

    @BeforeEach
    void setUp() {
        transferMapper = new TransferMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTransferSample1();
        var actual = transferMapper.toEntity(transferMapper.toDto(expected));
        assertTransferAllPropertiesEquals(expected, actual);
    }
}
