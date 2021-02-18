package org.perpetualnetworks.mdcrawlerconsumer.database.factory;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.DatabaseProvider;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Component
@Singleton
public class DataSourceFactories {

    private final Map<DatabaseProvider, DataSourceFactory> factories;

    public DataSourceFactories(
            @Named(Constants.DataSources.MYSQL) DataSourceFactory mysqlDatasourceFactory) {
        this.factories = getCurrentDataSources(mysqlDatasourceFactory);
    }

    @NotNull
    private ImmutableMap<DatabaseProvider, DataSourceFactory> getCurrentDataSources(
            DataSourceFactory mysqlDatasourceFactory) {
        return ImmutableMap.of(DatabaseProvider.MYSQL, mysqlDatasourceFactory);
    }

    public DataSource getDataSourceForDatabase(Database db) {
        final DataSourceFactory dataSourceFactory = factories
                .get(db.getDatabaseProvider());
        return dataSourceFactory.getDataSource(db.getDatabaseName());
    }
}
