package org.perpetualnetworks.mdcrawlerconsumer.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.config.CrawlerConsumerConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.KeywordDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactories;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.MysqlDataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.KeywordRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStoreImpl;
import org.perpetualnetworks.mdcrawlerconsumer.utils.ByteOperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
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
        String selectAll = "SELECT * FROM api_keyword";


        try (
                Connection conn = DriverManager.getConnection(config.getConnectionUrl(), config.getProperties());
                PreparedStatement ps = conn.prepareStatement(selectAll);
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

    @Disabled("works with local data")
    @Test
    @SneakyThrows
    void queryByEntity() {
        SessionFactoryStoreImpl factoryStore = new SessionFactoryStoreImpl(new DataSourceFactories(
                new MysqlDataSourceFactory(Collections.singletonList(config))));

        int testId = 8092;
        final SessionFactory sessionFactory = factoryStore.getSessionFactory(Database.CRAWLER_CONSUMER);
        final Session session = sessionFactory.openSession();
        final KeywordEntity keyWordEntity = session.get(KeywordEntity.class, testId);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("keyword: " + mapper.writeValueAsString(keyWordEntity));
    }
    @Disabled("works with local data")
    @Test
    @SneakyThrows
    void queryByEntity_keywords() {
        SessionFactoryStoreImpl factoryStore = new SessionFactoryStoreImpl(new DataSourceFactories(
                new MysqlDataSourceFactory(Collections.singletonList(config))));
        KeywordRepository keywordRepository = new KeywordRepository(new KeywordDao(), new SessionExecutor(factoryStore));

        final List<KeywordEntity> keywordEntities = keywordRepository.fetchAllKeywords();
        Pattern p = Pattern.compile("^[\\[].*");
        List<KeywordEntity> unConverted = keywordEntities.stream()
                .filter(word -> p.matcher(word.getWord()).matches())
                .map(keyword -> {
                    String newWord = ByteOperations.convertStringBytesToString(keyword.getWord());
                    return keywordRepository.updateWord(keyword, newWord);
                })
                .collect(Collectors.toList());
        System.out.println("unconverted size: " + unConverted.size());
        System.out.println("unconverted item: " + unConverted.stream().findAny());
    }
}
