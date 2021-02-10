package org.perpetualnetworks.mdcrawlerconsumer.database.factory;

import org.perpetualnetworks.mdcrawlerconsumer.config.DatabaseConfiguration;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class MysqlDataSource implements DataSource {
    private final DatabaseConfiguration databaseConfiguration;

    public MysqlDataSource(DatabaseConfiguration databaseConfiguration) {
        //TODO: use DI
        this.databaseConfiguration = databaseConfiguration;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseConfiguration.getConnectionUrl(), databaseConfiguration.getProperties());
    }

    @Override
    public Connection getConnection(String s, String s1) throws SQLException {
        return DriverManager.getConnection(databaseConfiguration.getConnectionUrl(), databaseConfiguration.getProperties());
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter printWriter) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int i) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger("mysqlDataSource");
    }

    @Override
    public <T> T unwrap(Class<T> aClass) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
        return false;
    }
}
