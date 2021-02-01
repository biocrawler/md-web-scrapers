package org.perpetualnetworks.mdcrawlerconsumer.config;

import java.util.Properties;

public interface DatabaseConfiguration {

    Properties getProperties();

    String getDatabaseName();

    String getConnectionUrl();
}
