package org.perpetualnetworks.mdcrawlerconsumer.database;

import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.annotation.Nonnull;

@Getter
public enum Database {

    CRAWLER_CONSUMER(Constants.DatabaseName.CRAWLER_CONSUMER,
            Constants.DatabaseSchema.CRAWLER_CONSUMER,
            DatabaseProvider.MYSQL);

    @Nonnull
    private final String databaseName;

    private final String databaseSchema;

    private final DatabaseProvider databaseProvider;

    Database(@Nonnull String databaseName, String databaseSchema, DatabaseProvider databaseProvider) {
        this.databaseName = databaseName;
        this.databaseSchema = databaseSchema;
        this.databaseProvider = databaseProvider;
    }
}
