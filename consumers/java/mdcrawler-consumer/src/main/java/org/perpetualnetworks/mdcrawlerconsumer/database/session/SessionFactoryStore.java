package org.perpetualnetworks.mdcrawlerconsumer.database.session;

import com.google.common.base.Preconditions;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.factory.DataSourceFactories;
import org.reflections.Reflections;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.util.EnumMap;
import java.util.Set;

@Singleton
public class SessionFactoryStore {

    @Nonnull
    private final EnumMap<Database, SessionFactory> factories = new EnumMap<>(Database.class);

    private final DataSourceFactories dataSourceFactories;

    @Inject
    public SessionFactoryStore(DataSourceFactories dataSourceFactories) {
        this.dataSourceFactories = dataSourceFactories;
        init();
    }

    private void init() {
        for (Database db : Database.values()) {
            DataSource dataSource = dataSourceFactories.getDataSourceForDatabase(db);

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure(db.getHibernateConfigName())
                    .applySetting(AvailableSettings.DATASOURCE, dataSource)
                    .build();

            MetadataSources metadataSources = new MetadataSources(registry);

            Reflections reflections = new Reflections("com.booking.amparsers.database.model");
            Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Table.class);
            for (Class<?> controller : annotated) {
                Table table = controller.getAnnotation(Table.class);
                if (db.getDatabaseSchema().equals(table.schema())) {
                    metadataSources = metadataSources.addAnnotatedClass(controller);
                }
            }

            SessionFactory sessionFactory = metadataSources.buildMetadata().buildSessionFactory();

            factories.put(db, sessionFactory);
        }
    }

    @Nonnull
    public SessionFactory getSessionFactory(@Nonnull Database db) {
        Preconditions.checkArgument(factories.containsKey(db),
                String.format("Can't find session factory for db %s", db.getDatabaseName()));
        return factories.get(db);
    }
}
