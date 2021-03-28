package org.perpetualnetworks.mdcrawler.metrics;

import io.micrometer.core.instrument.MockClock;
import io.micrometer.graphite.GraphiteConfig;
import io.netty.channel.ChannelOption;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.configs.GraphiteTestConfigFactory;
import org.perpetualnetworks.mdcrawler.integration.UdpTestServer;
import org.perpetualnetworks.mdcrawler.services.metrics.graphite.PerpetualGraphiteMeterRegistry;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

import java.net.DatagramSocket;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricsIntegrationTest {

    private final MockClock mockClock = new MockClock();

    GraphiteTestConfigFactory configFactory = new GraphiteTestConfigFactory();

    @Test
    void metricPrefixes() throws InterruptedException {
        final CountDownLatch receiveLatch = new CountDownLatch(1);

        final GraphiteConfig tagPrefixedConfig = configFactory.getTagPrefixedConfig();
        final PerpetualGraphiteMeterRegistry registry = new PerpetualGraphiteMeterRegistry(
                tagPrefixedConfig, mockClock);

        Connection server = UdpServer.create()
                .option(ChannelOption.SO_REUSEADDR, true)
                .host("localhost")
                .port(tagPrefixedConfig.port())
                .handle((in, out) -> {
                    in.receive()
                            .asString()
                            .subscribe(line -> {
                                assertTrue(line.startsWith("my.timer"));
                                receiveLatch.countDown();
                            });
                    return Flux.never();
                })
                .bind()
                .doOnSuccess(v -> {
                    registry.timer("my.timer")
                            .record(1, TimeUnit.MILLISECONDS);
                    registry.close();
                })
                .block(Duration.ofSeconds(10));

        assertFalse(receiveLatch.await(2, TimeUnit.SECONDS), "line was received");
        server.dispose();
        registry.close();
    }


    @Test
    void taggedMetrics() throws InterruptedException {
        final CountDownLatch receiveLatch = new CountDownLatch(1);

        final GraphiteConfig config = configFactory.getNonPrefixedConfig();
        final PerpetualGraphiteMeterRegistry registry = new PerpetualGraphiteMeterRegistry(
                config, mockClock);

        Connection server = UdpServer.create()
                .option(ChannelOption.SO_REUSEADDR, true)
                .host("localhost")
                .port(config.port())
                .handle((in, out) -> {
                    in.receive()
                            .asString()
                            .subscribe(line -> {
                                //assertTrue(line.startsWith("my.timer;key=value;metricattribute=max "));
                                //System.out.println(line);
                                assertTrue(line.startsWith("my.timer.max 1.00"));
                                receiveLatch.countDown();
                            });
                    return Flux.never();
                })
                .bind()
                .doOnSuccess(v -> {
                    registry.timer("my.timer")
                            .record(1, TimeUnit.MILLISECONDS);
                    registry.close();
                })
                .block(Duration.ofSeconds(10));

        assertTrue(receiveLatch.await(10, TimeUnit.SECONDS), "line was received");
        server.dispose();
        registry.close();
    }
    @Test
    void alice() {
        final GraphiteConfig config = configFactory.getTagPrefixedConfig();
        final PerpetualGraphiteMeterRegistry registry = new PerpetualGraphiteMeterRegistry(
                config, mockClock);
        UdpTestServer server = new UdpTestServer(config.port());
        server.runServerWithAssertion(line -> {
           System.out.println(line);
           assertTrue(line.startsWith("my"));
        }, v -> {
            registry.timer("my.timer").record(1, TimeUnit.MILLISECONDS);
            registry.close();
        });
    }
}

