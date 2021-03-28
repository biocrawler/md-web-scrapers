package org.perpetualnetworks.mdcrawler.services.metrics;

public interface MetricsService {

    void incrementArticleSendSuccessCount();

    void incrementArticleSendErrorCount();

    void sendArticleSendSum(double Sum);
}
