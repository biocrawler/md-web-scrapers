package org.perpetualnetworks.mdcrawlerconsumer;

public interface Constants {

    interface Application {
        String NAME = "mdcrawler-consumer";
    }

    interface DataSources {
        String MYSQL = "mysqlDataSourceFactory";
    }

    interface DatabaseName {
        String CRAWLER_CONSUMER = "mdcrawler_consumer_d";
    }

    interface DatabaseSchema {
        String DBO = "dbo";
    }

    interface HibernateConfig {
        String CRAWLER_CONSUMER = "hibernate_crawler_consumer.cfg.xml";
    }
}
