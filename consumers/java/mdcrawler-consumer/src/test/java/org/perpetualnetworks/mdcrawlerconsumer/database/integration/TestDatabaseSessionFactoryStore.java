package org.perpetualnetworks.mdcrawlerconsumer.database.integration;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.hibernate.SessionFactory;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.config.DatabaseConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStore;
import org.perpetualnetworks.mdcrawlerconsumer.database.testentity.AccountEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.testentity.EmployeeEntity;

import javax.annotation.Nonnull;

public class TestDatabaseSessionFactoryStore implements SessionFactoryStore {

    private static TestDatabaseSessionFactoryStore instance;
    private final DatabaseConfiguration databaseConfiguration;
    @Getter
    private final SessionFactory sessionFactory;

    public TestDatabaseSessionFactoryStore(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
        ImmutableSet.Builder<Class<?>> imutableSet = ImmutableSet.builder();
        imutableSet.addAll(Constants.DatabaseEntities.DEFAULT);
        imutableSet.add(AccountEntity.class);
        imutableSet.add(EmployeeEntity.class);
        TestDatabaseInitializer testDatabaseInitializer = new TestDatabaseInitializer(
                databaseConfiguration, imutableSet.build()
        );
        this.sessionFactory = testDatabaseInitializer.setupSessionFactory();
    }

    public TestDatabaseSessionFactoryStore instance() {
        if (instance == null) {
            instance = new TestDatabaseSessionFactoryStore(this.databaseConfiguration);
        }
        return instance;
    }


    public SessionFactory getSessionFactory(@Nonnull Database db) {
        return this.getSessionFactory();
    }
}
