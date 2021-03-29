package org.perpetualnetworks.mdcrawler.services.metrics;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;
import org.perpetualnetworks.mdcrawler.config.GraphiteConfiguration;
import org.perpetualnetworks.mdcrawler.services.metrics.graphite.PerpetualGraphiteMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;

public class MetricsServiceImpl implements MetricsService {

    private final Counter mendeleyArticleSendSumCounter;
    private final Counter figshareArticleSendSumCounter;
    private final Counter articleSendErrorCounter;
    private final Counter articleSendSuccessCounter;
    private final Counter figshareConversionSuccessCounter;
    private final Counter figshareConversionErrorCounter;
    private final Counter figshareArticlesWithFilesCounter;
    private final Counter figshareArticleBatchCounter;

    private final Counter mendeleyResponseSuccessCounter;
    private final Counter mendeleyResponseErrorCounter;
    private final PerpetualGraphiteMeterRegistry graphiteMeterRegistry;

    @Autowired
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
        mendeleyArticleSendSumCounter = Metrics.counter(MetricPaths.MENDELEY_ARTICLE_SEND_SUM.getPath());
        figshareArticleSendSumCounter = Metrics.counter(MetricPaths.FIGSHARE_ARTICLE_SEND_SUM.getPath());
        figshareConversionSuccessCounter = Metrics.counter(MetricPaths.FIGSHARE_ARTICLE_CONVERSION_SUCCESS.getPath());
        figshareConversionErrorCounter = Metrics.counter(MetricPaths.FIGSHARE_ARTICLE_CONVERSION_ERROR.getPath());
        figshareArticlesWithFilesCounter = Metrics.counter(MetricPaths.FIGSHARE_ARTICLE_WITH_FILES.getPath());
        figshareArticleBatchCounter = Metrics.counter(MetricPaths.FIGSHARE_ARTICLE_BATCH_COUNT.getPath());
        mendeleyResponseSuccessCounter = Metrics.counter(MetricPaths.MENDELEY_RESPONSE_SUCCESS.getPath());
        mendeleyResponseErrorCounter = Metrics.counter(MetricPaths.MENDELEY_RESPONSE_SUCCESS.getPath());

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
    public void sumMendeleyArticleSendSum(double sum) {
        mendeleyArticleSendSumCounter.increment(sum);
    }

    @Override
    public void sumFigshareArticleSendSum(double sum) {
        figshareArticleSendSumCounter.increment(sum);
    }

    @Override
    public void incrementFigshareArticleConversionErrorCount() {
        figshareConversionErrorCounter.increment();
    }

    @Override
    public void incrementFigshareArticleConversionSuccessCount() {
        figshareConversionSuccessCounter.increment();
    }

    @Override
    public void incrementFigshareArticleWithFilesCount() {
        figshareArticlesWithFilesCounter.increment();
    }

    @Override
    public void incrementFigshareArticleBatchCount() {
        figshareArticleBatchCounter.increment();
    }

    @Override
    public void incrementMendeleyResponseSuccess() {
        mendeleyResponseSuccessCounter.increment();
    }

    @Override
    public void incrementMendeleyResponseError() {
        mendeleyResponseErrorCounter.increment();
    }

    //TODO: check where needed
    public void close() {
        graphiteMeterRegistry.close();
        graphiteMeterRegistry.stop();
    }
}
