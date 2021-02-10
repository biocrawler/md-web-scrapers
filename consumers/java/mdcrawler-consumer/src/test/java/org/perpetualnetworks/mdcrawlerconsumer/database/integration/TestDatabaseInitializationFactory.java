package org.perpetualnetworks.mdcrawlerconsumer.database.integration;

import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.MysqlDataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.integration.configuration.EmbeddedDbConfiguration;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class TestDatabaseInitializationFactory {

    private final DataSource dataSource;
    private final EmbeddedDbConfiguration configuration;
    private StandardServiceRegistry standardServiceRegistry;
    private Properties properties;

    public TestDatabaseInitializationFactory(EmbeddedDbConfiguration configuration) {

        this.dataSource = new MysqlDataSourceFactory(Collections.singletonList(configuration)).getDataSource(configuration.getDatabaseName());
        this.configuration = configuration;

    }

    public void configureDataSourceFactoryAndInitSchema() {
        applySettings();
        runMigration();
    }

    private Properties createAndSetProperties() {
        final Properties properties = new Properties();
        properties.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "managed");
        properties.put(AvailableSettings.GENERATE_STATISTICS, "false");
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.put("jadira.usertype.autoRegisterUserTypes", "true");
        properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
        // TODO: it should be uncommented but it does not work with Json type
        // properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put(AvailableSettings.DEFAULT_SCHEMA, configuration.getDatabaseName());
        //TODO: check if required
        this.properties = properties;
        return properties;

    }

    public SessionFactory getSessionFactory(List<Class<?>> entities) {
        final Configuration configuration = new Configuration(new BootstrapServiceRegistryImpl());
        configuration.setProperties(properties);
        entities.forEach(configuration::addAnnotatedClass);
        return configuration.buildSessionFactory(standardServiceRegistry);
    }

    private void runMigration() {

        Flyway flyway = Flyway.configure()
                .schemas(configuration.getDatabaseName())
                .dataSource(dataSource)
                .configuration(properties)
                .load();
        flyway.migrate();
    }

    private void applySettings() {
        Properties properties = createAndSetProperties();
        StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
        standardServiceRegistryBuilder.configure();
        standardServiceRegistryBuilder.applySettings(properties);
        standardServiceRegistry = standardServiceRegistryBuilder.build();
    }
}
