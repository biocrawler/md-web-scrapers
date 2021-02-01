package org.perpetualnetworks.mdcrawlerconsumer.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.config.CrawlerConsumerConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.FileArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.FileArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactories;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.MysqlDataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.FileArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStore;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

public class EntityTests {

    private static final String CONNECTION_URL = "jdbc:mysql://localhost:3306/mdcrawler_consumer_d?serverTimezone=UTC";
    private static final CrawlerConsumerConfiguration config = CrawlerConsumerConfiguration.builder()
            .dbCredentialsFile("config/consumerdb.json")
            .connectionUrl(CONNECTION_URL)
            .databaseName("mdcrawler_consumer_d")
            .build();

    @Disabled("works with local db")
    @Test
    void listAllEntities() {
        final SessionFactoryStore sessionFactory = buildLocalSessionFactory();
        final Session session = sessionFactory.getSessionFactory(Database.CRAWLER_CONSUMER)
                .openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<FileArticleEntity> cq = cb.createQuery(FileArticleEntity.class);
        Root<FileArticleEntity> rootEntry = cq.from(FileArticleEntity.class);
        CriteriaQuery<FileArticleEntity> all = cq.select(rootEntry);

        TypedQuery<FileArticleEntity> allQuery = session.createQuery(all);
        final List<FileArticleEntity> resultList = allQuery.getResultList();
        System.out.println(resultList);

    }

    @Disabled("works with local db")
    @Test
    void repositoryTest() {
        final SessionFactoryStore sessionFactory = buildLocalSessionFactory();
        SessionExecutor se = new SessionExecutor(sessionFactory);
        FileArticleRepository repository = new FileArticleRepository(
                new FileArticleDao(), se
        );
        final List<FileArticleEntity> bob = repository.fetchArticleFiles("bob");
        System.out.println(bob);

    }

    private SessionFactoryStore buildLocalSessionFactory() {
        return new SessionFactoryStore(new DataSourceFactories(
                new MysqlDataSourceFactory(Collections.singletonList(config))));
    }
}
