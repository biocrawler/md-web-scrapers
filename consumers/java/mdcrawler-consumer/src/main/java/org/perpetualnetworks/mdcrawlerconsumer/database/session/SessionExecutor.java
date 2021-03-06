package org.perpetualnetworks.mdcrawlerconsumer.database.session;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Slf4j
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

    public <T> T executeAndReturnTransactionalRW(Function<Session, T> query, Database database) {
        Transaction transaction = null;
        try (Session session = sessionFactoryStore.getSessionFactory(database).openSession()) {
            transaction = session.beginTransaction();
            T result = query.apply(session);
            transaction.commit();
            return result;
        } catch (Exception e) {
            rollback(transaction);
            throw new RuntimeException("Exception while executing query. Transaction rolled back.", e);
        }
    }

    private void rollback(Transaction transaction) {
        try {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        } catch (Exception e) {
            log.warn("Exception while rolling back transaction.", e);
        }
    }
}
