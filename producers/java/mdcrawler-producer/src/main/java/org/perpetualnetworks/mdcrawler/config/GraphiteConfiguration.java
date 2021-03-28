package org.perpetualnetworks.mdcrawler.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteProtocol;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@ConfigurationProperties("graphite")
@Data
@Builder
public class GraphiteConfiguration {
    @JsonProperty
    String host;
    @JsonProperty
    int port;

    public GraphiteConfig toGraphiteConfig() {
        return new GraphiteConfig() {
            @Override
            public String get(@Nonnull String key) {
                return key;
            }

            @Override
            public boolean graphiteTagsEnabled() {
                return true;
            }

            @Override
            @Nonnull
            public Duration step() {
                return Duration.ofSeconds(1);
            }

            @Override
            @Nonnull
            public String host() {
                return host;
            }

            @Override
            public int port() {
                return port;
            }

            @Override
            public boolean enabled() {
                return true;
            }

            @Override
            @Nonnull
            public GraphiteProtocol protocol() {
                return GraphiteProtocol.UDP;
            }

            @Override
            @Nonnull
            public Validated<?> validate() {
                return Validated.none();
            }

            @Override
            @Nonnull
            public String[] tagsAsPrefix() {
                return new String[]{};
            }

            @Override
            @Nonnull
            public TimeUnit rateUnits() {
                return TimeUnit.MILLISECONDS;
            }

            @Override
            @Nonnull
            public TimeUnit durationUnits() {
                return TimeUnit.MILLISECONDS;
            }
        };
    }
}
