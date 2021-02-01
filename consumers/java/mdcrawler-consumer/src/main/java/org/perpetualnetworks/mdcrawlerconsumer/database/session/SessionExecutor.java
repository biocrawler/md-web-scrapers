package org.perpetualnetworks.mdcrawlerconsumer.database.session;

import org.hibernate.Session;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStore;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.function.Function;

@Component
public class SessionExecutor {
    private final SessionFactoryStore sessionFactoryStore;

    public SessionExecutor(SessionFactoryStore sessionFactoryStore) {
        this.sessionFactoryStore = sessionFactoryStore;
    }

    public <T> T executeAndReturn(Function<Session, T> query, Database database) {
        try (Session session = sessionFactoryStore.getSessionFactory(database).openSession()) {
            return query.apply(session);
        }
    }
}
