package org.perpetualnetworks.mdcrawlerconsumer.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.config.CrawlerConsumerConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleFileDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.TestDatabaseInitializer;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleFileRepository;
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
        CriteriaQuery<ArticleFileEntity> cq = cb.createQuery(ArticleFileEntity.class);
        Root<ArticleFileEntity> rootEntry = cq.from(ArticleFileEntity.class);
        CriteriaQuery<ArticleFileEntity> all = cq.select(rootEntry);

        TypedQuery<ArticleFileEntity> allQuery = session.createQuery(all);
        final List<ArticleFileEntity> resultList = allQuery.getResultList();
        System.out.println(resultList);

    }

    @Disabled("works with local db")
    @SneakyThrows
    @Test
    void repositoryTest_fileArticle() {
        //TODO: fix infinite recursion
        final SessionFactoryStoreImpl sessionFactory = buildLocalSessionFactoryStore();
        SessionExecutor se = new SessionExecutor(sessionFactory);
        ArticleFileRepository repository = new ArticleFileRepository(
                new ArticleFileDao(), se);
        final List<ArticleFileEntity> bob = repository.fetchArticleFiles("bob");
        final List<ArticleFileEntity> alice = repository.fetchAllArticleFiles();
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

    @Disabled("works with local db")
    @SneakyThrows
    @Test
    void repositoryTest_article_repository() {
        int testArticleId = 38750;
        final SessionFactoryStoreImpl sessionFactory = buildLocalSessionFactoryStore();
        SessionExecutor se = new SessionExecutor(sessionFactory);
        ObjectMapper mapper = new ObjectMapper();
        ArticleRepository articleRepository = new ArticleRepository(new ArticleDao(), se);
        final List<ArticleEntity> articleEntities = articleRepository.fetchArticle(String.valueOf(testArticleId));
        Converter converter = new Converter();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                converter.convert(articleEntities.get(0))));

    }

    @Disabled("works with local db")
    @SneakyThrows
    @Test
    void repositoryTest_article_repository_all() {
        final SessionFactoryStoreImpl sessionFactory = buildLocalSessionFactoryStore();
        SessionExecutor se = new SessionExecutor(sessionFactory);
        ObjectMapper mapper = new ObjectMapper();
        ArticleRepository articleRepository = new ArticleRepository(new ArticleDao(), se);
        final List<ArticleEntity> articleEntities = articleRepository.fetchAllArticles();
        Converter converter = new Converter();
        for (ArticleEntity ae: articleEntities) {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(
                    converter.convert(ae)));
        }
    }

    private SessionFactoryStoreImpl buildLocalSessionFactoryStore() {
        return new TestDatabaseInitializer(config, Constants.DatabaseEntities.DEFAULT).buildSessionFactoryStore();
    }
}
