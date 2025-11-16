package sn.ondmoney.txe.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.txe.domain.CashOutTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.txe.web.rest.TestUtil;

class CashOutTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CashOut.class);
        CashOut cashOut1 = getCashOutSample1();
        CashOut cashOut2 = new CashOut();
        assertThat(cashOut1).isNotEqualTo(cashOut2);

        cashOut2.setId(cashOut1.getId());
        assertThat(cashOut1).isEqualTo(cashOut2);

        cashOut2 = getCashOutSample2();
        assertThat(cashOut1).isNotEqualTo(cashOut2);
    }
}
