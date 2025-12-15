package sn.ondmoney.txe.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.txe.domain.TransactionTestSamples.*;
import static sn.ondmoney.txe.domain.TransferTestSamples.*;
import static sn.ondmoney.txe.domain.WalletTestSamples.*;

import java.util.HashSet;
import java.util.Set;
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

    @Test
    void debitsTest() {
        Wallet wallet = getWalletRandomSampleGenerator();
        Transaction transactionBack = getTransactionRandomSampleGenerator();

        wallet.addDebits(transactionBack);
        assertThat(wallet.getDebits()).containsOnly(transactionBack);
        assertThat(transactionBack.getDebitedAccount()).isEqualTo(wallet);

        wallet.removeDebits(transactionBack);
        assertThat(wallet.getDebits()).doesNotContain(transactionBack);
        assertThat(transactionBack.getDebitedAccount()).isNull();

        wallet.debits(new HashSet<>(Set.of(transactionBack)));
        assertThat(wallet.getDebits()).containsOnly(transactionBack);
        assertThat(transactionBack.getDebitedAccount()).isEqualTo(wallet);

        wallet.setDebits(new HashSet<>());
        assertThat(wallet.getDebits()).doesNotContain(transactionBack);
        assertThat(transactionBack.getDebitedAccount()).isNull();
    }

    @Test
    void creditsTest() {
        Wallet wallet = getWalletRandomSampleGenerator();
        Transaction transactionBack = getTransactionRandomSampleGenerator();

        wallet.addCredits(transactionBack);
        assertThat(wallet.getCredits()).containsOnly(transactionBack);
        assertThat(transactionBack.getCreditedAccount()).isEqualTo(wallet);

        wallet.removeCredits(transactionBack);
        assertThat(wallet.getCredits()).doesNotContain(transactionBack);
        assertThat(transactionBack.getCreditedAccount()).isNull();

        wallet.credits(new HashSet<>(Set.of(transactionBack)));
        assertThat(wallet.getCredits()).containsOnly(transactionBack);
        assertThat(transactionBack.getCreditedAccount()).isEqualTo(wallet);

        wallet.setCredits(new HashSet<>());
        assertThat(wallet.getCredits()).doesNotContain(transactionBack);
        assertThat(transactionBack.getCreditedAccount()).isNull();
    }

    @Test
    void transfersSentTest() {
        Wallet wallet = getWalletRandomSampleGenerator();
        Transfer transferBack = getTransferRandomSampleGenerator();

        wallet.addTransfersSent(transferBack);
        assertThat(wallet.getTransfersSents()).containsOnly(transferBack);
        assertThat(transferBack.getSender()).isEqualTo(wallet);

        wallet.removeTransfersSent(transferBack);
        assertThat(wallet.getTransfersSents()).doesNotContain(transferBack);
        assertThat(transferBack.getSender()).isNull();

        wallet.transfersSents(new HashSet<>(Set.of(transferBack)));
        assertThat(wallet.getTransfersSents()).containsOnly(transferBack);
        assertThat(transferBack.getSender()).isEqualTo(wallet);

        wallet.setTransfersSents(new HashSet<>());
        assertThat(wallet.getTransfersSents()).doesNotContain(transferBack);
        assertThat(transferBack.getSender()).isNull();
    }

    @Test
    void transfersReceivedTest() {
        Wallet wallet = getWalletRandomSampleGenerator();
        Transfer transferBack = getTransferRandomSampleGenerator();

        wallet.addTransfersReceived(transferBack);
        assertThat(wallet.getTransfersReceiveds()).containsOnly(transferBack);
        assertThat(transferBack.getReceiver()).isEqualTo(wallet);

        wallet.removeTransfersReceived(transferBack);
        assertThat(wallet.getTransfersReceiveds()).doesNotContain(transferBack);
        assertThat(transferBack.getReceiver()).isNull();

        wallet.transfersReceiveds(new HashSet<>(Set.of(transferBack)));
        assertThat(wallet.getTransfersReceiveds()).containsOnly(transferBack);
        assertThat(transferBack.getReceiver()).isEqualTo(wallet);

        wallet.setTransfersReceiveds(new HashSet<>());
        assertThat(wallet.getTransfersReceiveds()).doesNotContain(transferBack);
        assertThat(transferBack.getReceiver()).isNull();
    }
}
