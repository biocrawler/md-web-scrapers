package org.perpetualnetworks.mdcrawler.configs;

import io.micrometer.core.instrument.config.validate.Validated;
import io.micrometer.graphite.GraphiteConfig;
import io.micrometer.graphite.GraphiteProtocol;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.MathUtils;

import javax.annotation.Nonnull;
import java.net.DatagramSocket;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GraphiteTestConfigFactory {
    private static final String GRAPHITE_HOST = "192.168.5.213";
    private static final int GRAPHITE_PORT = 2003;
    private static final int randomPort = findAvailableUdpPort();

    public GraphiteTestConfigFactory() {
    }

    public static int findAvailableUdpPort() {
        int min = 1024;
        int randomValue = (MathUtils.randomInt(65535) + min) - min;
        try {
            final DatagramSocket datagramSocket = new DatagramSocket(randomValue);
            assert (datagramSocket.isBound());
            datagramSocket.close();
            return randomValue;
        } catch (Exception ignored) {
        }
        throw new RuntimeException("no available UDP port");
    }

    public GraphiteConfig getLocalGraphiteConfig() {
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
                return GRAPHITE_HOST;
            }

            @Override
            public int port() {
                return GRAPHITE_PORT;
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
                return TimeUnit.SECONDS;
            }

            @Override
            @Nonnull
            public TimeUnit durationUnits() {
                return TimeUnit.SECONDS;
            }
        };


    }

    public GraphiteConfig getTagPrefixedConfig() {
        return new GraphiteConfig() {
            @Override
            public String get(@Nonnull String key) {
                return null;
            }

            @Override
            public boolean graphiteTagsEnabled() {
                return false;
            }

            @Override
            public int port() {
                System.out.println("tag prefixed random port: " + randomPort);
                return randomPort;
            }

            @Override
            @Nonnull
            public Duration step() {
                return Duration.ofSeconds(1);
            }

            @Override
            @Nonnull
            public GraphiteProtocol protocol() {
                return GraphiteProtocol.UDP;
            }

            @Override
            @Nonnull
            public String[] tagsAsPrefix() {
                return new String[]{"application"};
            }
        };
    }

    public GraphiteConfig getNonPrefixedConfig() {
        return new GraphiteConfig() {
            @Override
            public String get(@Nonnull String key) {
                return null;
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
            public GraphiteProtocol protocol() {
                return GraphiteProtocol.UDP;
            }

            @Override
            public int port() {
                System.out.println("not tag prefixed random port: " + randomPort);
                return randomPort;
            }

            @Override
            @Nonnull
            public String[] tagsAsPrefix() {
                return new String[]{};
            }
        };
    }
}
