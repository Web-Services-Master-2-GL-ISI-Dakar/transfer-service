package sn.ondmoney.txe.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static sn.ondmoney.txe.domain.TransferTestSamples.*;
import static sn.ondmoney.txe.domain.WalletTestSamples.*;

import org.junit.jupiter.api.Test;
import sn.ondmoney.txe.web.rest.TestUtil;

class TransferTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transfer.class);
        Transfer transfer1 = getTransferSample1();
        Transfer transfer2 = new Transfer();
        assertThat(transfer1).isNotEqualTo(transfer2);

        transfer2.setId(transfer1.getId());
        assertThat(transfer1).isEqualTo(transfer2);

        transfer2 = getTransferSample2();
        assertThat(transfer1).isNotEqualTo(transfer2);
    }

    @Test
    void senderTest() {
        Transfer transfer = getTransferRandomSampleGenerator();
        Wallet walletBack = getWalletRandomSampleGenerator();

        transfer.setSender(walletBack);
        assertThat(transfer.getSender()).isEqualTo(walletBack);

        transfer.sender(null);
        assertThat(transfer.getSender()).isNull();
    }

    @Test
    void receiverTest() {
        Transfer transfer = getTransferRandomSampleGenerator();
        Wallet walletBack = getWalletRandomSampleGenerator();

        transfer.setReceiver(walletBack);
        assertThat(transfer.getReceiver()).isEqualTo(walletBack);

        transfer.receiver(null);
        assertThat(transfer.getReceiver()).isNull();
    }
}
