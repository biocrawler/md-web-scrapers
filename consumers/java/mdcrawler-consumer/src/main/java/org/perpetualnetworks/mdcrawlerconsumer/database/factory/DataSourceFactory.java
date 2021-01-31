package org.perpetualnetworks.mdcrawlerconsumer.database.factory;

import javax.sql.DataSource;

public interface DataSourceFactory {

    DataSource getDataSource(String databaseName);
}
