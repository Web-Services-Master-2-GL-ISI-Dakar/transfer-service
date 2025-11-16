package sn.ondmoney.txe.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class CashOutTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CashOut getCashOutSample1() {
        return new CashOut().id(1L).senderId(1L).receiverId(1L);
    }

    public static CashOut getCashOutSample2() {
        return new CashOut().id(2L).senderId(2L).receiverId(2L);
    }

    public static CashOut getCashOutRandomSampleGenerator() {
        return new CashOut().id(longCount.incrementAndGet()).senderId(longCount.incrementAndGet()).receiverId(longCount.incrementAndGet());
    }
}
