package org.perpetualnetworks.mdcrawlerconsumer.database.integration;

import lombok.Synchronized;
import org.hibernate.SessionFactory;
import org.perpetualnetworks.mdcrawlerconsumer.config.DatabaseConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStoreImpl;

import java.util.ArrayList;
import java.util.Set;

public class TestDatabaseInitializer {
    private static SessionFactory sessionFactory;
    private final Set<Class<?>> entitiesClass;
    private final TestDatabaseInitializationFactory testDatabaseInitializationFactory;
    //private final Environment environment;

    public TestDatabaseInitializer(DatabaseConfiguration databaseConfiguration, Set<Class<?>> entities ){//final Class<?> entity, final Class<?>... entities) {
        //this.entityClass = entity;
        this.entitiesClass = entities;
        //TODO: add some metrics
        //this.environment = new Environment("hibernate-test-util", new ObjectMapper(), null, null, //new MetricRegistry(),
        //        Thread.currentThread().getContextClassLoader());

        testDatabaseInitializationFactory = new TestDatabaseInitializationFactory(databaseConfiguration);
        testDatabaseInitializationFactory.configureDataSourceFactoryAndInitSchema();
    }

    @Synchronized
    public SessionFactory setupSessionFactory() {
        sessionFactory = sessionFactory != null ? sessionFactory : buildSessionFactory();
        return sessionFactory;
    }


    private SessionFactory buildSessionFactory() {
        return testDatabaseInitializationFactory.getSessionFactory(new ArrayList<>(entitiesClass));
    }

    public SessionFactoryStoreImpl buildSessionFactoryStore() {
        return testDatabaseInitializationFactory.getSessionFactoryStore();
    }
}
