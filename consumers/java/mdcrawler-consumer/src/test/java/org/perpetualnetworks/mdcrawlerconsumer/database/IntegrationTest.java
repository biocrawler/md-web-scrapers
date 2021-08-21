package org.perpetualnetworks.mdcrawlerconsumer.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.config.CrawlerConsumerConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.consumers.AwsSqsConsumer;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.TestDatabaseInitializer;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.TestDatabaseSessionFactoryStore;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.configuration.EmbeddedDbConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.defaults.ArticleDefaults;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.services.AwsSqsService;
import org.perpetualnetworks.mdcrawlerconsumer.utils.lzw.LZwCompressor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class IntegrationTest {

    public static final EmbeddedDbConfiguration EMBEDDED_DB_CONFIGURATION = EmbeddedDbConfiguration.builder().build();
    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/mdcrawler_consumer_d?serverTimezone=UTC";
    private static final CrawlerConsumerConfiguration LOCAL_DB_CONFIGURATION = CrawlerConsumerConfiguration.builder()
            .dbCredentialsFile("config/consumerdb.json")
            .connectionUrl(CONNECTION_URL)
            .databaseName("mdcrawler_consumer_d")
            .build();

    @Test
    void embeddedDbTest() {
        TestDatabaseSessionFactoryStore tdbStore = new TestDatabaseSessionFactoryStore(EMBEDDED_DB_CONFIGURATION);
        final SessionFactory sessionFactory = tdbStore.getSessionFactory();
        final Session session = sessionFactory.openSession();
        session.close();
    }

    @Test
    @SneakyThrows
    void save_retrieve() {
        SessionExecutor se = buildEmbeddedSessionFactoryStore();
        ArticleRepository articleRepository = new ArticleRepository(new ArticleDao(), se);
        Integer response = articleRepository.saveOrUpdate(ArticleDefaults.anArticle().build());
        System.out.println("response: " + response);
        final List<ArticleEntity> entities = articleRepository.fetchAllArticles();
        Converter converter = new Converter();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entities.stream().map(converter::convert).collect(Collectors.toSet())));
    }

    private SessionExecutor buildEmbeddedSessionFactoryStore() {
        return new SessionExecutor(
                new TestDatabaseInitializer(
                        EMBEDDED_DB_CONFIGURATION, Constants.DatabaseEntities.DEFAULT)
                        .buildSessionFactoryStore());
    }

    private SessionExecutor buildLocalSessionFactoryStore() {
        return new SessionExecutor(
                new TestDatabaseInitializer(
                        LOCAL_DB_CONFIGURATION, Constants.DatabaseEntities.DEFAULT)
                        .buildSessionFactoryStore());
    }

    @Disabled("full consumer test, works with sqs queue and local db")
    @SneakyThrows
    @Test
    void messageConsumerIntegerationTest() {
        SessionExecutor se = buildLocalSessionFactoryStore();
        ArticleRepository articleRepository = new ArticleRepository(new ArticleDao(), se);
        AwsSqsConsumer consumer = getConsumer();
        final List<Article> articles = consumer.fetchArticles(1);
        for(Article article: articles) {
            Integer articleId = articleRepository.saveOrUpdate(article);
            log.info("article saved with id: " + articleId);
            ObjectMapper mapper = new ObjectMapper();
            Converter converter = new Converter();
            final Optional<ArticleEntity> articleEntities = articleRepository.fetchArticle(String.valueOf(articleId));
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(articleEntities.stream()
                    .map(converter::convert).collect(Collectors.toList())));
        }

    }

    private AwsSqsConsumer getConsumer() {
        AwsSqsService awsSqsService = new AwsSqsService(
                AwsConfiguration.builder()
                        .sqsUrl("https://sqs.eu-central-1.amazonaws.com/397254617684/crawler_queue")
                        .credentialsFile("config/aws.json")
                        .region("eu-central-1")
                        .build(), new LZwCompressor());
        return new AwsSqsConsumer(awsSqsService);
    }

}
