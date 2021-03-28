package org.perpetualnetworks.mdcrawler.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.graphite.GraphiteConfig;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.configs.GraphiteTestConfigFactory;
import org.perpetualnetworks.mdcrawler.services.metrics.graphite.PerpetualGraphiteMeterRegistry;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
    GraphiteTestConfigFactory configFactory = new GraphiteTestConfigFactory();
    final GraphiteConfig config = configFactory.getLocalGraphiteConfig();

    PerpetualGraphiteMeterRegistry graphiteMeterRegistry = new PerpetualGraphiteMeterRegistry(config, clock);

    @Test
    void bob() {
        compositeRegistry.add(graphiteMeterRegistry);
        Metrics.addRegistry(graphiteMeterRegistry);

        //this increments the metricPath by 2
        final String metricPath = "test.alice.instance";
        final Counter counter = Metrics.counter(metricPath);
        counter.increment();
        counter.increment(1.0);

        // it is possible to find a counter based on path
        //final Counter counter = Metrics.globalRegistry
        //        .find(metricPath).counter();

        System.out.println("graphite counter count: " + counter.count());
        System.out.println("graphite measurement: " + counter.measure());

        //timer recording amounts:
        final String timerName = "my.timer";
        final Timer timer = graphiteMeterRegistry.timer(timerName);
        timer.record(1, TimeUnit.MILLISECONDS);
        timer.record(1, TimeUnit.MILLISECONDS);

        System.out.println("counter value in gmr miliseconds: " + timer.count());


        System.out.println("counter value in gmr miliseconds: " + timer.count());

        //simple counter
        Counter simpleCounter = oneSimpleMeter.counter("my.timer.simple");
        simpleCounter.increment();
        simpleCounter.increment();
        System.out.println("simple measurement: " + simpleCounter.measure());


        System.out.println("simple meters: " + oneSimpleMeter.getMeters());
        Metrics.addRegistry(oneSimpleMeter);

        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();


        final int availableProcessors = osBean.getAvailableProcessors();
        final String name = osBean.getName();
        final double systemLoadAverage = osBean.getSystemLoadAverage();
        final String arch = osBean.getArch();
        final String version = osBean.getVersion();
        //System.out.println( osBean.getProcessCpuLoad());
        //System.out.println( osBean.getSystemCpuLoad());

        System.out.println("processors " + availableProcessors + " name: " + name + " load " + systemLoadAverage + " arch/version"
                + arch + " / " + version);
        Gauge gauge = Gauge.builder("test.cpu.consumption", () -> new Number() {
            @Override
            public int intValue() {
                return (int) systemLoadAverage;
            }

            @Override
            public long longValue() {
                return (long) systemLoadAverage;
            }

            @Override
            public float floatValue() {
                return (float) systemLoadAverage;
            }

            @Override
            public double doubleValue() {
                return systemLoadAverage;
            }
        })
                .register(graphiteMeterRegistry);


        System.out.println("gauge value: " + gauge.value());
        System.out.println("gauge measurement: " + gauge.value());


        //}

        graphiteMeterRegistry.close();
        graphiteMeterRegistry.stop();
    }


    @Test
    void printUsage() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("get")
                    && Modifier.isPublic(method.getModifiers())) {
                Object value;
                try {
                    value = method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = e;
                } // try
                System.out.println(method.getName() + " = " + value);
            } // if
        } // for
    }
}
