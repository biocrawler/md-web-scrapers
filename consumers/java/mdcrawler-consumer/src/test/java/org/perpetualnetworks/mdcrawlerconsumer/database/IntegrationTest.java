package org.perpetualnetworks.mdcrawlerconsumer.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.TestDatabaseInitializer;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.TestDatabaseSessionFactoryStore;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.configuration.EmbeddedDbConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.defaults.ArticleDefaults;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class IntegrationTest {

    public static final EmbeddedDbConfiguration CONFIG = EmbeddedDbConfiguration.builder().build();

    @Test
    void embeddedDbTest() {
        TestDatabaseSessionFactoryStore tdbStore = new TestDatabaseSessionFactoryStore(CONFIG);
        final SessionFactory sessionFactory = tdbStore.getSessionFactory();
        final Session session = sessionFactory.openSession();
        session.close();
        log.info("session closed");
    }

    @Test
    @SneakyThrows
    void save_retrieve() {
        SessionExecutor se = buildLocalSessionFactoryStore();
        ArticleRepository articleRepository = new ArticleRepository(new ArticleDao(), se);
        Integer response = articleRepository.saveOrUpdate(ArticleDefaults.anArticle().build());
        System.out.println("response: " + response);
        final List<ArticleEntity> entities = articleRepository.fetchAllArticles();
        Converter converter = new Converter();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(entities.stream().map(converter::convert).collect(Collectors.toSet())));
    }

    private SessionExecutor buildLocalSessionFactoryStore() {
        return new SessionExecutor(
                new TestDatabaseInitializer(
                        CONFIG, Constants.DatabaseEntities.DEFAULT)
                        .buildSessionFactoryStore());
    }

}
