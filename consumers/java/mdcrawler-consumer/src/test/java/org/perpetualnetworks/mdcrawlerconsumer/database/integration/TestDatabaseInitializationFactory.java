package org.perpetualnetworks.mdcrawlerconsumer.database.integration;

import lombok.extern.slf4j.Slf4j;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.BootstrapServiceRegistryImpl;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.perpetualnetworks.mdcrawlerconsumer.config.DatabaseConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactories;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.MysqlDataSourceFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStoreImpl;
import org.perpetualnetworks.mdcrawlerconsumer.utils.InlineQueryLogEntryCreator;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Slf4j
public class TestDatabaseInitializationFactory {

    private final DataSource dataSource;
    private final DatabaseConfiguration configuration;
    private StandardServiceRegistry standardServiceRegistry;
    private Properties properties;

    public TestDatabaseInitializationFactory(DatabaseConfiguration configuration) {

        this.dataSource = getProxyDataSource(new MysqlDataSourceFactory(Collections.singletonList(configuration))
                .getDataSource(configuration.getDatabaseName()));
        this.configuration = configuration;

    }

    public void configureDataSourceFactoryAndInitSchema() {
        createAndSetProperties();
        applySettings();
        runMigration();
    }

    private void createAndSetProperties() {
        final Properties properties = new Properties();
        properties.put(AvailableSettings.DATASOURCE, dataSource);
        properties.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "managed");
        properties.put(AvailableSettings.GENERATE_STATISTICS, "false");
        properties.put(AvailableSettings.SHOW_SQL, "true");
        properties.put(AvailableSettings.FORMAT_SQL, "true");
        properties.put(AvailableSettings.USE_SQL_COMMENTS, "true");
        properties.put("jadira.usertype.autoRegisterUserTypes", "true");
        properties.put(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect");
        // TODO: it should be uncommented but it does not work with Json type
        // properties.put("hibernate.hbm2ddl.auto", "validate");
        //properties.put(AvailableSettings.DEFAULT_SCHEMA, configuration.getDatabaseName());
        //TODO: check if required
        this.properties = properties;

    }

    public SessionFactory getSessionFactory(List<Class<?>> entities) {
        final Configuration configuration = new Configuration(new BootstrapServiceRegistryImpl());

        //TODO: add validatoion constraints for classes
        //HibernateValidatorConfiguration validatorConfig = Validation.byProvider(HibernateValidator.class).configure();
        //ConstraintMapping m = validatorConfig.createConstraintMapping();
        //for (Class<?> c : entities) {
        //    m.constraintDefinition(c.getAnnotation());
        //    m.type(c);
        //}
        //Validator validator = validatorConfig.addMapping(m).buildValidatorFactory().getValidator();
        entities.forEach(configuration::addAnnotatedClass);
        return configuration.buildSessionFactory(standardServiceRegistry);
    }

    public SessionFactoryStoreImpl getSessionFactoryStore() {
        return new SessionFactoryStoreImpl(new DataSourceFactories(databaseName -> dataSource));
    }

    private void runMigration() {

        try {
            Flyway flyway = Flyway.configure()
                    .schemas(configuration.getDatabaseName())
                    .dataSource(dataSource)
                    .configuration(properties)
                    .load();
            flyway.migrate();
        } catch (FlywayException e) {
            log.error("could not initialize migration");
        }
    }

    private void applySettings() {
        StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();
        standardServiceRegistryBuilder.configure();
        standardServiceRegistryBuilder.applySettings(properties);
        standardServiceRegistry = standardServiceRegistryBuilder.build();
    }

    private ProxyDataSource getProxyDataSource(DataSource ds) {
        SLF4JQueryLoggingListener loggingListener = new SLF4JQueryLoggingListener();
        loggingListener.setQueryLogEntryCreator(new InlineQueryLogEntryCreator());
        return ProxyDataSourceBuilder
                .create(ds)
                .name("sqlCommandInterceptor")
                .listener(loggingListener)
                .build();

    }
}
