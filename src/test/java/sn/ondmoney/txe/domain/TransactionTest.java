package sn.ondmoney.txe.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.txe.domain.TransactionTestSamples.*;
import static sn.ondmoney.txe.domain.WalletTestSamples.*;

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

    @Test
    void debitedAccountTest() {
        Transaction transaction = getTransactionRandomSampleGenerator();
        Wallet walletBack = getWalletRandomSampleGenerator();

        transaction.setDebitedAccount(walletBack);
        assertThat(transaction.getDebitedAccount()).isEqualTo(walletBack);

        transaction.debitedAccount(null);
        assertThat(transaction.getDebitedAccount()).isNull();
    }

    @Test
    void creditedAccountTest() {
        Transaction transaction = getTransactionRandomSampleGenerator();
        Wallet walletBack = getWalletRandomSampleGenerator();

        transaction.setCreditedAccount(walletBack);
        assertThat(transaction.getCreditedAccount()).isEqualTo(walletBack);

        transaction.creditedAccount(null);
        assertThat(transaction.getCreditedAccount()).isNull();
    }
}
