package org.perpetualnetworks.mdcrawlerconsumer.database.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Environment;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactories;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.MysqlDataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.configuration.EmbeddedDbConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TestDatabaseInitializer {
    private static SessionFactory sessionFactory;
    private final Class<?> entityClass;
    private final Class<?>[] entitiesClass;
    private final EmbeddedDbConfiguration configuration;
    private final TestDatabaseInitializationFactory testDatabaseInitializationFactory;
    //private final Environment environment;

    public TestDatabaseInitializer(String databaseName, final Class<?> entity, final Class<?>... entities) {
        this.entityClass = entity;
        this.entitiesClass = entities;
        this.configuration = EmbeddedDbConfiguration.builder().databaseName(databaseName).build();
        //TODO: add some metrics
        //this.environment = new Environment("hibernate-test-util", new ObjectMapper(), null, null, //new MetricRegistry(),
        //        Thread.currentThread().getContextClassLoader());

        testDatabaseInitializationFactory = new TestDatabaseInitializationFactory(configuration);
        testDatabaseInitializationFactory.configureDataSourceFactoryAndInitSchema();
    }

    @Synchronized
    public SessionFactory setupSessionFactory() {
        sessionFactory = sessionFactory != null ? sessionFactory : buildSessionFactory();
        return sessionFactory;
    }

    private SessionFactory buildSessionFactory() {
        final List<Class<?>> entities = Arrays.stream(entitiesClass).collect(Collectors.toList());
        entities.add(entityClass);
        return testDatabaseInitializationFactory.getSessionFactory(entities);

    }
}
