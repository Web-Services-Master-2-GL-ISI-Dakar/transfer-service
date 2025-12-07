package sn.ondmoney.txe.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.ondmoney.txe.web.rest.TestUtil;

class WalletDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(WalletDTO.class);
        WalletDTO walletDTO1 = new WalletDTO();
        walletDTO1.setId(1L);
        WalletDTO walletDTO2 = new WalletDTO();
        assertThat(walletDTO1).isNotEqualTo(walletDTO2);
        walletDTO2.setId(walletDTO1.getId());
        assertThat(walletDTO1).isEqualTo(walletDTO2);
        walletDTO2.setId(2L);
        assertThat(walletDTO1).isNotEqualTo(walletDTO2);
        walletDTO1.setId(null);
        assertThat(walletDTO1).isNotEqualTo(walletDTO2);
    }
}
