package org.perpetualnetworks.mdcrawlerconsumer;

import org.reflections.Reflections;

import javax.persistence.Table;
import java.util.Set;

public interface Constants {

    String TOP_LEVEL_ENTITY_PATH = "org.perpetualnetworks.mdcrawlerconsumer";


    interface DataSources {
        String MYSQL = "mysqlDataSourceFactory";
    }

    interface DatabaseName {
        String CRAWLER_CONSUMER = "mdcrawler_consumer_d";
    }

    interface DatabaseSchema {
        String CRAWLER_CONSUMER = "mdcrawler_consumer_d";
    }

    interface DatabaseEntities {
        Reflections reflections = new Reflections(TOP_LEVEL_ENTITY_PATH);
        Set<Class<?>> DEFAULT = reflections.getTypesAnnotatedWith(Table.class);
    }

    interface Time {
        String IsoPattern = "dd-MM-yyyy'T'HH:mm:ss";
        String AlternatePattern = "yyyy-MM-dd'T'HH:mm:ss.sss";
        String dateStringsPattern = "EEE MMM dd hh:mm:ss ZZZ yyyy";
    }
}
