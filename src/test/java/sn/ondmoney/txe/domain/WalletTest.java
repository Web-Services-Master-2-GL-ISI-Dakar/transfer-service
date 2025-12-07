package sn.ondmoney.txe.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.txe.domain.WalletTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.txe.web.rest.TestUtil;

class WalletTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Wallet.class);
        Wallet wallet1 = getWalletSample1();
        Wallet wallet2 = new Wallet();
        assertThat(wallet1).isNotEqualTo(wallet2);

        wallet2.setId(wallet1.getId());
        assertThat(wallet1).isEqualTo(wallet2);

        wallet2 = getWalletSample2();
        assertThat(wallet1).isNotEqualTo(wallet2);
    }
}
