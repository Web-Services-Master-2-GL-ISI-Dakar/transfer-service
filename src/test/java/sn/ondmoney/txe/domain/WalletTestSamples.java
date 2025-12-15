package sn.ondmoney.txe.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WalletTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Wallet getWalletSample1() {
        return new Wallet().id(1L).userId("userId1").phone("phone1").version(1);
    }

    public static Wallet getWalletSample2() {
        return new Wallet().id(2L).userId("userId2").phone("phone2").version(2);
    }

    public static Wallet getWalletRandomSampleGenerator() {
        return new Wallet()
            .id(longCount.incrementAndGet())
            .userId(UUID.randomUUID().toString())
            .phone(UUID.randomUUID().toString())
            .version(intCount.incrementAndGet());
    }
}
