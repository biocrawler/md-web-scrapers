package org.perpetualnetworks.mdcrawler.services.metrics;

public interface MetricsService {

    void incrementArticleSendSuccessCount();

    void incrementArticleSendErrorCount();

    void sumMendeleyArticleSendSum(double sum);

    void sumFigshareArticleSendSum(double sum);

    void incrementFigshareArticleConversionErrorCount();

    void incrementFigshareArticleConversionSuccessCount();

    void incrementFigshareArticleWithFilesCount();

    void incrementMendeleyResponseSuccess();

    void incrementMendeleyResponseError();
}
