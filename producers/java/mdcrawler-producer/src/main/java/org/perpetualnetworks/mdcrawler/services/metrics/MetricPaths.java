package org.perpetualnetworks.mdcrawler.services.metrics;

import lombok.Getter;

public enum MetricPaths {
    PRODUCER_ARTICLE_SEND_SUCCESS("mdcrawler.fosshost.producer.send.article.success"),
    PRODUCER_ATICLE_SEND_ERROR("mdcrawler.fosshost.producer.send.article.fail"),
    PRODUCER_ARTICLE_SEND_SUM("mdcrawler.fosshost.producer.send.article.sum"),
    ;

    @Getter
    private final String path;

    MetricPaths(String path) {
        this.path = path;
    }

}
