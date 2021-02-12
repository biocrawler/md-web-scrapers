package org.perpetualnetworks.mdcrawlerconsumer.database;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.TestDatabaseSessionFactoryStore;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.configuration.EmbeddedDbConfiguration;

@Slf4j
public class IntegrationTest {

    @Test
    void embeddedDbTest() {
        EmbeddedDbConfiguration config = EmbeddedDbConfiguration.builder().build();
        TestDatabaseSessionFactoryStore tdbStore = new TestDatabaseSessionFactoryStore(config);
        final SessionFactory sessionFactory = tdbStore.getSessionFactory();
        final Session session = sessionFactory.openSession();
        session.close();
        log.info("session closed");
    }

}
