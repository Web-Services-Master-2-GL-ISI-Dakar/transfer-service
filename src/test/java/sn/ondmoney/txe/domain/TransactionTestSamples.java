package sn.ondmoney.txe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Transaction getTransactionSample1() {
        return new Transaction()
            .id(1L)
            .txId("txId1")
            .externalTxId("externalTxId1")
            .source("source1")
            .destination("destination1")
            .errorMessage("errorMessage1");
    }

    public static Transaction getTransactionSample2() {
        return new Transaction()
            .id(2L)
            .txId("txId2")
            .externalTxId("externalTxId2")
            .source("source2")
            .destination("destination2")
            .errorMessage("errorMessage2");
    }

    public static Transaction getTransactionRandomSampleGenerator() {
        return new Transaction()
            .id(longCount.incrementAndGet())
            .txId(UUID.randomUUID().toString())
            .externalTxId(UUID.randomUUID().toString())
            .source(UUID.randomUUID().toString())
            .destination(UUID.randomUUID().toString())
            .errorMessage(UUID.randomUUID().toString());
    }
}
