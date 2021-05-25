package org.perpetualnetworks.mdcrawler.scrapers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.perpetualnetworks.mdcrawler.client.FigshareApiClient;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleResponse;
import org.perpetualnetworks.mdcrawler.publishers.AwsSqsPublisher;
import org.perpetualnetworks.mdcrawler.services.metrics.MetricsService;

import java.util.Set;

class FigshareApiScraperTest {
    @Mock
    MetricsService metricsService;
    @Mock
    AwsSqsPublisher publisher;

    FigshareApiScraper figshareApiScraper = new FigshareApiScraper(new FigshareApiClient(new OkHttpClient()),
            publisher, metricsService);

    @Disabled
    @Test
    @SneakyThrows
    void fetchArticlesForDefaultTerms() {
        final Set<ArticleResponse> articleResponses = figshareApiScraper.fetchArticlesForDefaultTerms();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(articleResponses.iterator().next()));
        final int size = articleResponses.size();
        System.out.println("responses size: " + size);

    }
}