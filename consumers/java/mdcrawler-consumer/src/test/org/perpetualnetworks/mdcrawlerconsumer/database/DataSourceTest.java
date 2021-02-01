package org.perpetualnetworks.mdcrawlerconsumer.database;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.config.CrawlerConsumerConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeyWordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactories;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.MysqlDataSource;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.MysqlDataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStore;

import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

@Slf4j
//@SpringBootTest
public class DataSourceTest {

    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/mdcrawler_consumer_d?serverTimezone=UTC";
    private static final CrawlerConsumerConfiguration config = CrawlerConsumerConfiguration.builder()
                .dbCredentialsFile("config/consumerdb.json")
                .connectionUrl(CONNECTION_URL)
                .databaseName("mdcrawler_consumer_d")
                .build();

    @Disabled("works with local db")
    @Test
    void queryBySql() {
        String sqlSelectAllPersons = "SELECT * FROM api_keyword";


        try (
                Connection conn = DriverManager.getConnection(config.getConnectionUrl(), config.getProperties());
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
    void queryByEntity() {
        SessionFactoryStore factoryStore = new SessionFactoryStore(new DataSourceFactories(
                new MysqlDataSourceFactory(Collections.singletonList(config))));

        long testId = 8092;
        final SessionFactory sessionFactory = factoryStore.getSessionFactory(Database.CRAWLER_CONSUMER);
        final Session session = sessionFactory.openSession();
        final KeyWordEntity keyWordEntity = session.get(KeyWordEntity.class, testId);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("keyword: " + mapper.writeValueAsString(keyWordEntity));
    }
}
