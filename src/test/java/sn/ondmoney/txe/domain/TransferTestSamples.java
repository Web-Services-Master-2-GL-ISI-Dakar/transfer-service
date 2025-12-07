package sn.ondmoney.txe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TransferTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Transfer getTransferSample1() {
        return new Transfer()
            .id(1L)
            .txId("txId1")
            .externalTxId("externalTxId1")
            .senderPhone("senderPhone1")
            .receiverPhone("receiverPhone1")
            .errorMessage("errorMessage1");
    }

    public static Transfer getTransferSample2() {
        return new Transfer()
            .id(2L)
            .txId("txId2")
            .externalTxId("externalTxId2")
            .senderPhone("senderPhone2")
            .receiverPhone("receiverPhone2")
            .errorMessage("errorMessage2");
    }

    public static Transfer getTransferRandomSampleGenerator() {
        return new Transfer()
            .id(longCount.incrementAndGet())
            .txId(UUID.randomUUID().toString())
            .externalTxId(UUID.randomUUID().toString())
            .senderPhone(UUID.randomUUID().toString())
            .receiverPhone(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString());
    }
}
