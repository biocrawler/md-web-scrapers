package org.perpetualnetworks.mdcrawlerconsumer.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.config.CrawlerConsumerConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.FileArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.FileArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.TestDatabaseInitializer;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.FileArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStoreImpl;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Slf4j
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
        final SessionFactoryStoreImpl sessionFactory = buildLocalSessionFactoryStore();
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
    @SneakyThrows
    @Test
    void repositoryTest_fileArticle() {
        //TODO: fix infinite recursion
        final SessionFactoryStoreImpl sessionFactory = buildLocalSessionFactoryStore();
        SessionExecutor se = new SessionExecutor(sessionFactory);
        FileArticleRepository repository = new FileArticleRepository(
                new FileArticleDao(), se);
        final List<FileArticleEntity> bob = repository.fetchArticleFiles("bob");
        final List<FileArticleEntity> alice = repository.fetchAllArticleFiles();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bob));
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(alice));

    }

    @Disabled("works with local db")
    @SneakyThrows
    @Test
    void repositoryTest_article() {
        int testArticleId = 38750;
        final SessionFactoryStoreImpl sessionFactory = buildLocalSessionFactoryStore();
        ObjectMapper mapper = new ObjectMapper();
        Session session = sessionFactory.getSessionFactory(Database.CRAWLER_CONSUMER).openSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<ArticleEntity> cq = cb.createQuery(ArticleEntity.class);
        Root<ArticleEntity> rootEntry = cq.from(ArticleEntity.class);
        CriteriaQuery<ArticleEntity> all = cq.select(rootEntry).where(cb.equal(rootEntry.get("id"), testArticleId));

        TypedQuery<ArticleEntity> allQuery = session.createQuery(all);
        final List<ArticleEntity> resultList = allQuery.getResultList();
        Optional<ArticleEntity> anyArticle = resultList.stream().findAny();
        final ArticleEntity articleEntity = anyArticle.get();
        System.out.println(articleEntity.getKeywordRelations().stream().findAny().get().getKeywordEntity().getId());
    }

    private SessionFactoryStoreImpl buildLocalSessionFactoryStore() {
        return new TestDatabaseInitializer(config, Constants.DatabaseEntities.DEFAULT).buildSessionFactoryStore();
    }
}
