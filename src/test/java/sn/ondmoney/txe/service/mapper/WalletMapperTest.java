package sn.ondmoney.txe.service.mapper;

import static sn.ondmoney.txe.domain.WalletAsserts.*;
import static sn.ondmoney.txe.domain.WalletTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletMapperTest {

    private WalletMapper walletMapper;

    @BeforeEach
    void setUp() {
        walletMapper = new WalletMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getWalletSample1();
        var actual = walletMapper.toEntity(walletMapper.toDto(expected));
        assertWalletAllPropertiesEquals(expected, actual);
    }
}
