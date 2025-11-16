package sn.ondmoney.txe.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import sn.ondmoney.txe.web.rest.TestUtil;

class CashOutDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashOutDTO.class);
        CashOutDTO cashOutDTO1 = new CashOutDTO();
        cashOutDTO1.setId(1L);
        CashOutDTO cashOutDTO2 = new CashOutDTO();
        assertThat(cashOutDTO1).isNotEqualTo(cashOutDTO2);
        cashOutDTO2.setId(cashOutDTO1.getId());
        assertThat(cashOutDTO1).isEqualTo(cashOutDTO2);
        cashOutDTO2.setId(2L);
        assertThat(cashOutDTO1).isNotEqualTo(cashOutDTO2);
        cashOutDTO1.setId(null);
        assertThat(cashOutDTO1).isNotEqualTo(cashOutDTO2);
    }
}
