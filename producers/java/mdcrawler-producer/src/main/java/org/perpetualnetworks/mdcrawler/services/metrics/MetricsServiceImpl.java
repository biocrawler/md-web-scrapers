package org.perpetualnetworks.mdcrawler.services.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.perpetualnetworks.mdcrawler.config.GraphiteConfiguration;
import org.perpetualnetworks.mdcrawler.services.metrics.graphite.PerpetualGraphiteMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;

public class MetricsServiceImpl implements MetricsService {

    @Autowired
    GraphiteConfiguration graphiteConfiguration;

    private final Counter articleSumCounter;
    private final Counter articleSendErrorCounter;
    private final Counter articleSendSuccessCounter;
    private final PerpetualGraphiteMeterRegistry graphiteMeterRegistry;

    public MetricsServiceImpl(GraphiteConfiguration graphiteConfiguration) {
        Clock clock = new Clock() {
            @Override
            public long wallTime() {
                return System.currentTimeMillis();
            }

            @Override
            public long monotonicTime() {
                return SYSTEM.monotonicTime();
            }
        };
        graphiteMeterRegistry = new PerpetualGraphiteMeterRegistry(graphiteConfiguration.toGraphiteConfig(), clock);
        Metrics.addRegistry(graphiteMeterRegistry);
        articleSendSuccessCounter = Metrics.counter(MetricPaths.PRODUCER_ARTICLE_SEND_SUCCESS.getPath());
        articleSendErrorCounter = Metrics.counter(MetricPaths.PRODUCER_ATICLE_SEND_ERROR.getPath());
        articleSumCounter = Metrics.counter(MetricPaths.PRODUCER_ARTICLE_SEND_SUM.getPath());

    }

    @Override
    public void incrementArticleSendSuccessCount() {
        articleSendSuccessCounter.increment();
    }

    @Override
    public void incrementArticleSendErrorCount() {
        articleSendErrorCounter.increment();
    }

    @Override
    public void sendArticleSendSum(double sum) {
        articleSumCounter.increment(sum);
    }

    public void close() {
        graphiteMeterRegistry.close();
        graphiteMeterRegistry.stop();
    }
}
