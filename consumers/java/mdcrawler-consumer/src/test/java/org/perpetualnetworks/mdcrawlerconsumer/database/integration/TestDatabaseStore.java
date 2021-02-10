package org.perpetualnetworks.mdcrawlerconsumer.database.integration;

import lombok.Getter;
import org.hibernate.SessionFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.*;

public class TestDatabaseStore {

    private static TestDatabaseStore instance;
    private final String databaseName;
    @Getter
    private final SessionFactory sessionFactory;

    public TestDatabaseStore(String databaseName) {
        this.databaseName = databaseName;
        TestDatabaseInitializer testDatabaseInitializer = new TestDatabaseInitializer(
                databaseName,
                ArticleEntity.class,
                AuthorEntity.class,
                FileArticleEntity.class,
                KeyWordEntity.class,
                ArticleAuthorRelationEntity.class,
                ArticleKeywordRelationEntity.class,
                FileKeywordRelationEntity.class
        );
        this.sessionFactory = testDatabaseInitializer.setupSessionFactory();
    }

    public TestDatabaseStore instance() {
        if (instance == null) {
            instance = new TestDatabaseStore(this.databaseName);
        }
        return instance;
    }
}
