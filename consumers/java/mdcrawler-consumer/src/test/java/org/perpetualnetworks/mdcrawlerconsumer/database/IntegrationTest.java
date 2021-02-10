package org.perpetualnetworks.mdcrawlerconsumer.database;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.TestDatabaseStore;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
public class IntegrationTest {

    @Test
    void embeddedDbTest() {
        TestDatabaseStore tdbStore = new TestDatabaseStore(Constants.DatabaseName.CRAWLER_CONSUMER);
        final SessionFactory sessionFactory = tdbStore.getSessionFactory();
        final Session session = sessionFactory.openSession();
        session.close();
        log.info("session closed");
    }

}
