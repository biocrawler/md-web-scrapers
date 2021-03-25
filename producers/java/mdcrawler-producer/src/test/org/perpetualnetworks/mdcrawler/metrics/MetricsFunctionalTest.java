package org.perpetualnetworks.mdcrawler.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.graphite.GraphiteConfig;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.configs.GraphiteTestConfigFactory;
import org.perpetualnetworks.mdcrawler.services.metrics.graphite.PerpetualGraphiteMeterRegistry;

import java.util.concurrent.TimeUnit;

public class MetricsFunctionalTest {
    CompositeMeterRegistry compositeRegistry = new CompositeMeterRegistry();
    SimpleMeterRegistry oneSimpleMeter = new SimpleMeterRegistry();
    private final Clock clock = new Clock() {
        @Override
        public long wallTime() {
            return System.currentTimeMillis();
        }

        @Override
        public long monotonicTime() {
            return SYSTEM.monotonicTime();
        }
    };
    final String metricPath = "test.alice.instance";
    GraphiteTestConfigFactory configFactory = new GraphiteTestConfigFactory();
    final GraphiteConfig config = configFactory.getLocalGraphiteConfig();

    PerpetualGraphiteMeterRegistry graphiteMeterRegistry = new PerpetualGraphiteMeterRegistry(config, clock);

    class CountedObject {
        private CountedObject() {
            Metrics.counter(metricPath).increment(1.0);
        }
    }

    @Test
    void bob() {
        compositeRegistry.add(oneSimpleMeter);
        compositeRegistry.add(graphiteMeterRegistry);

        Metrics.addRegistry(graphiteMeterRegistry);
        //graphiteMeterRegistry.start();
        Metrics.counter(metricPath).increment();
        new CountedObject();
        new CountedObject();
        new CountedObject();
        new CountedObject();
        // try {
        //     Metrics.more().wait(2000L);
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        final Counter counter = Metrics.globalRegistry
                .find(metricPath).counter();
        assert counter != null;
        graphiteMeterRegistry.timer("my.timer")
                .record(1, TimeUnit.MILLISECONDS);
        graphiteMeterRegistry.timer("my.timer")
                .record(1, TimeUnit.MILLISECONDS);
        graphiteMeterRegistry.close();
        System.out.println(counter.count());
        counter.measure().forEach(System.out::println);
        graphiteMeterRegistry.stop();

        // compositeRegistry.getRegistries().forEach(reg -> reg.counter("bob.counter").increment());
        // Metrics.addRegistry(oneSimpleMeter);

        // Metrics.counter("bob.instance").increment();
        // new CountedObject();

        // Counter counter = Metrics.globalRegistry
        //         .find("bonb.instance").counter();
        // assert counter != null;
        // assertEquals(counter.count(), 2.0);
    }
}
