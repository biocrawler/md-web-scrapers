package org.perpetualnetworks.mdcrawlerconsumer.database.factory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractDataSourceFactory implements DataSourceFactory {

    private final Map<String, DataSource> datasources;

    public AbstractDataSourceFactory() {
        datasources = new HashMap<>();
    }

    @Override
    public DataSource getDataSource(String databaseName) {
        if (Objects.nonNull(datasources.get(databaseName))) {
            return datasources.get(databaseName);
        }
        return createDataSourceAndStore(databaseName);
    }

    abstract DataSource createDataSource(String databaseName);

    private DataSource createDataSourceAndStore(String databaseName) {
        DataSource datasource = createDataSource(databaseName);
        datasources.put(databaseName, datasource);
        return datasource;
    }
}
