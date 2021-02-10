package org.perpetualnetworks.mdcrawlerconsumer.database.integration.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.config.DatabaseConfiguration;

import java.util.Properties;

@AllArgsConstructor
@Value
@Builder
public class EmbeddedDbConfiguration implements DatabaseConfiguration {
    @JsonProperty
    Properties properites = getProperites();

    @JsonProperty
    String databaseName;

    @JsonProperty
    String driverClass = "org.h2.Driver";

    @JsonProperty
    String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=MySQL";

    @JsonProperty
    String user = "sa";

    @JsonProperty
    String password = "";

    @JsonProperty
    String validationQuery = "SELECT 1";

    @Override
    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put("driverClass", getDriverClass());
        properties.put("url", getUrl());
        properties.put("user", getUser());
        properties.put("password", getPassword());
        return properties;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getConnectionUrl() {
        return getUrl();
    }

}
