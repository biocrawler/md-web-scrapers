package org.perpetualnetworks.mdcrawlerconsumer.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Properties;

@Data
@Component
@ConfigurationProperties("crawler-consumer")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CrawlerConsumerConfiguration extends AbstractDatabaseConfiguration implements DatabaseConfiguration {
    @JsonProperty
    @Nonnull
    private String dbCredentialsFile;
    @JsonProperty
    private String connectionUrl;
    @JsonProperty
    private String databaseName;
    @JsonProperty
    private Properties properties;

    public Properties getProperties() {
        return parseProperties(dbCredentialsFile);
    }
}
