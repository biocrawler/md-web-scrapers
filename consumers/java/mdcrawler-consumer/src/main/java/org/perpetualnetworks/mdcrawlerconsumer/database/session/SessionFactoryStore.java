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
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.inject.Singleton;
import javax.persistence.Table;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;
import java.util.EnumMap;
import java.util.Set;

@Component
@Singleton
public class SessionFactoryStore {

    public static final String TOP_LEVEL_ENTITY_PATH = "org.perpetualnetworks.mdcrawlerconsumer";
    @Nonnull
    private final EnumMap<Database, SessionFactory> factories = new EnumMap<>(Database.class);

    private final DataSourceFactories dataSourceFactories;

    public SessionFactoryStore(DataSourceFactories dataSourceFactories) {
        this.dataSourceFactories = dataSourceFactories;
        init();
    }

    private void init() {
        for (Database db : Database.values()) {
            DataSource dataSource = dataSourceFactories.getDataSourceForDatabase(db);

            MetadataSources metadataSources = buildMetaDataSources(db, dataSource);

            metadataSources = setMetadataSources(db, metadataSources);

            factories.put(db, metadataSources.buildMetadata().buildSessionFactory());
        }
    }

    @NotNull
    private MetadataSources buildMetaDataSources(Database db, DataSource dataSource) {
        StandardServiceRegistry registry = buildServiceRegistry(db, dataSource);
        return new MetadataSources(registry);
    }

    private MetadataSources setMetadataSources(Database db, MetadataSources metadataSources) {
        Reflections reflections = new Reflections(TOP_LEVEL_ENTITY_PATH);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Table.class);
        for (Class<?> controller : annotated) {
            Table table = controller.getAnnotation(Table.class);
            if (db.getDatabaseSchema().equals(table.schema())) {
                metadataSources = metadataSources.addAnnotatedClass(controller);
            }
        }
        return metadataSources;
    }

    private StandardServiceRegistry buildServiceRegistry(Database db, DataSource dataSource) {
        return new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.DATASOURCE, dataSource)
                .build();
    }

    @Nonnull
    public SessionFactory getSessionFactory(@Nonnull Database db) {
        Preconditions.checkArgument(factories.containsKey(db),
                String.format("Can't find session factory for db %s", db.getDatabaseName()));
        return factories.get(db);
    }
}
