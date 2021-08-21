package org.perpetualnetworks.mdcrawlerconsumer.config;

import org.flywaydb.core.Flyway;
import org.perpetualnetworks.mdcrawlerconsumer.consumers.AwsSqsConsumer;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactories;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.MysqlDataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStoreImpl;
import org.perpetualnetworks.mdcrawlerconsumer.utils.lzw.LZwCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class AppConfiguration {

    @Autowired
    CrawlerConsumerConfiguration crawlerConsumerConfiguration;

    @Autowired
    AwsConfiguration awsConfiguration;

    @Bean
    public DataSourceFactories getDataSourceFactories() {
        return new DataSourceFactories(
                new MysqlDataSourceFactory(Collections.singletonList(crawlerConsumerConfiguration)));
    }

    @Bean
    public Flyway getFlyway() {
        return Flyway.configure().load();
    }

    @Bean
    public FlywayMigrationStrategy getFlywayMigrationStrategy() {
        return flyway -> {
            // do nothing
        };
    }


    @Bean
    FlywayMigrationInitializer getFlywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway, (f) -> {
        });
    }


    @Bean
    FlywayMigrationInitializer delayedFlywayInitializer(Flyway flyway) {
        return getFlywayInitializer(flyway);
    }

    @Bean
    AwsSqsConsumer getAwsSqsConsumer() {
        return new AwsSqsConsumer(awsConfiguration, new LZwCompressor());
    }

    @Bean
    ArticleRepository getArticleRepository() {
        SessionExecutor sessionExecutor = new SessionExecutor(new SessionFactoryStoreImpl(getDataSourceFactories()));
        return new ArticleRepository(new ArticleDao(), sessionExecutor);
    }
}
