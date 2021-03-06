package org.perpetualnetworks.mdcrawler.services.metrics;

import lombok.Getter;

public enum MetricPaths {
    PRODUCER_ARTICLE_SEND_SUCCESS("mdcrawler.fosshost.producer.send.article.success"),
    PRODUCER_ATICLE_SEND_ERROR("mdcrawler.fosshost.producer.send.article.fail"),

    FIGSHARE_ARTICLE_CONVERSION_ERROR("mdcrawler.fosshost.figshare.converter.article.error"),
    FIGSHARE_ARTICLE_CONVERSION_SUCCESS("mdcrawler.fosshost.figshare.converter.article.success"),
    FIGSHARE_ARTICLE_WITH_FILES("mdcrawler.fosshost.figshare.converter.article.hasfiles"),

    FIGSHARE_ARTICLE_BATCH_COUNT("mdcrawler.fosshost.figshare.scraper.batch"),

    MENDELEY_ARTICLE_SEND_SUM("mdcrawler.fosshost.mendeley.send.article.sum"),
    FIGSHARE_ARTICLE_SEND_SUM("mdcrawler.fosshost.figshare.send.article.sum"),

    MENDELEY_RESPONSE_SUCCESS("mdcrawler.fosshost.mendeley.response.success"),

    FIGSHARE_API_ARTICLE_SEND_SUM("mdcrawler.fosshost.figshare-api.send.article.sum"),
    ;

    @Getter
    private final String path;

    MetricPaths(String path) {
        this.path = path;
    }

}
