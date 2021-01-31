package org.perpetualnetworks.mdcrawlerconsumer.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.config.CrawlerConsumerConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeyWord;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactories;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStore;

import javax.sql.DataSource;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

@Slf4j
//@SpringBootTest
public class DataSourceTest {

    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/mdcrawler_consumer_d?serverTimezone=UTC";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    static Properties PROPERTIES;

    static {
        CrawlerConsumerConfiguration config = CrawlerConsumerConfiguration.builder()
                .dbCredentialsFile("config/consumerdb.json")
                .build();
        try {
            //TODO: put into class
            JsonNode fileJson = MAPPER.readValue(new File(config.getDbCredentialsFile()), JsonNode.class);
            System.out.println(fileJson);
            PROPERTIES = new Properties();
            PROPERTIES.put("user", fileJson.get("user").textValue());
            PROPERTIES.put("password", fileJson.get("password").textValue());
        } catch (Exception e) {
            log.error("could not parse database json file", e);
        }
    }


    //@Disabled("works with local db")
    @Test
    void bob() {
        String sqlSelectAllPersons = "SELECT * FROM api_keyword";


        try (
                Connection conn = DriverManager.getConnection(CONNECTION_URL, PROPERTIES);
                PreparedStatement ps = conn.prepareStatement(sqlSelectAllPersons);
                ResultSet rs = ps.executeQuery()) {

            System.out.println("result set: " + rs);
            while (rs.next()) {
                long id = rs.getLong("ID");
                System.out.println(id);
            }
        } catch (SQLException e) {
            log.error("unable to complete request", e);
        }
    }

    @Test
    @SneakyThrows
    void alice() {
        SessionFactoryStore factoryStore = new SessionFactoryStore(new DataSourceFactories(new DataSourceFactory() {
            @Override
            public javax.sql.DataSource getDataSource(String databaseName) {
                return new DataSource() {
                    @Override
                    public <T> T unwrap(Class<T> aClass) throws SQLException {
                        return null;
                    }

                    @Override
                    public boolean isWrapperFor(Class<?> aClass) throws SQLException {
                        return false;
                    }

                    @Override
                    public Connection getConnection() throws SQLException {
                        return DriverManager.getConnection(CONNECTION_URL, PROPERTIES);
                    }

                    @Override
                    public Connection getConnection(String s, String s1) throws SQLException {
                        return DriverManager.getConnection(CONNECTION_URL, PROPERTIES.getProperty("user"), PROPERTIES.getProperty("password"));
                    }

                    @Override
                    public PrintWriter getLogWriter() throws SQLException {
                        return null;
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
                        return null;
                    }
                };
            }
        }));
        long testId = 8092;
        final SessionFactory sessionFactory = factoryStore.getSessionFactory(Database.CRAWLER_CONSUMER);
        final Session session = sessionFactory.openSession();
        final KeyWord keyWord = session.get(KeyWord.class, testId);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("keyword: " + mapper.writeValueAsString(keyWord));
    }
}
