package org.perpetualnetworks.mdcrawlerconsumer.database.factory;

import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.config.DatabaseConfiguration;

import javax.inject.Singleton;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;


@Singleton
@Slf4j
public class MysqlDataSourceFactory extends AbstractDataSourceFactory {

    private final List<DatabaseConfiguration> dbConfigurations;

    public MysqlDataSourceFactory(List<DatabaseConfiguration> dbConfigurations) {
        this.dbConfigurations = dbConfigurations;
    }

    DataSource createDataSource(String databaseName) {
        //StatementInterceptor.setGlobalInterceptor(EventStatementInterceptor.class);
        final Optional<DatabaseConfiguration> databaseConfigMatch = dbConfigurations.stream()
                .filter(config -> config.getDatabaseName().equals(databaseName))
                .findFirst();
        if (databaseConfigMatch.isEmpty()) {
            log.warn("config match not found for database: " + databaseName);
        }
        return new MysqlDataSource(databaseConfigMatch.get());
    }
}