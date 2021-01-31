package org.perpetualnetworks.mdcrawlerconsumer.database;

import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.annotation.Nonnull;

@Getter
public enum Database {

    CRAWLER_CONSUMER(Constants.DatabaseName.CRAWLER_CONSUMER,
            Constants.DatabaseSchema.CRAWLER_CONSUMER,
            DatabaseProvider.MYSQL,
            Constants.HibernateConfig.CRAWLER_CONSUMER);


    @Nonnull
    private final String databaseName;

    private final String databaseSchema;

    private final DatabaseProvider databaseProvider;

    private final String hibernateConfigName;

    Database(@Nonnull String databaseName, String databaseSchema, DatabaseProvider databaseProvider, String hibernateConfigName) {
        this.databaseName = databaseName;
        this.databaseSchema = databaseSchema;
        this.databaseProvider = databaseProvider;
        this.hibernateConfigName = hibernateConfigName;
    }
}
