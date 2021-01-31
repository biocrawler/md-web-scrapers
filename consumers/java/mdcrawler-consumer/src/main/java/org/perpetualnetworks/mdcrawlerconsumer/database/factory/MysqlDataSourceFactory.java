package org.perpetualnetworks.mdcrawlerconsumer.database.factory;

//import com.booking.jdbc.DataSources;
//import com.booking.jdbc.EventStatementInterceptor;
//import com.booking.jdbc.StatementInterceptor;


import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.inject.Singleton;
import javax.sql.DataSource;



@Singleton
public class MysqlDataSourceFactory extends AbstractDataSourceFactory {

    public MysqlDataSourceFactory() {
        //DataSources.init(Constants.Application.NAME);
    }

    DataSource createDataSource(String databaseName) {
        //StatementInterceptor.setGlobalInterceptor(EventStatementInterceptor.class);
        //return DataSources.instance().readOnly(databaseName);
        return null;
    }
}
