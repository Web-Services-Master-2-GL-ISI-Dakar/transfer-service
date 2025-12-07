package sn.ondmoney.txe.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.txe.domain.TransactionTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.txe.web.rest.TestUtil;

class TransactionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transaction.class);
        Transaction transaction1 = getTransactionSample1();
        Transaction transaction2 = new Transaction();
        assertThat(transaction1).isNotEqualTo(transaction2);

        transaction2.setId(transaction1.getId());
        assertThat(transaction1).isEqualTo(transaction2);

        transaction2 = getTransactionSample2();
        assertThat(transaction1).isNotEqualTo(transaction2);
    }
}
