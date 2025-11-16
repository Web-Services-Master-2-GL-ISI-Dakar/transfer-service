package sn.ondmoney.txe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import sn.ondmoney.txe.config.AsyncSyncConfiguration;
import sn.ondmoney.txe.config.EmbeddedElasticsearch;
import sn.ondmoney.txe.config.EmbeddedKafka;
import sn.ondmoney.txe.config.EmbeddedRedis;
import sn.ondmoney.txe.config.EmbeddedSQL;
import sn.ondmoney.txe.config.JacksonConfiguration;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { OndmoneyTransactionEngineApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedRedis
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
