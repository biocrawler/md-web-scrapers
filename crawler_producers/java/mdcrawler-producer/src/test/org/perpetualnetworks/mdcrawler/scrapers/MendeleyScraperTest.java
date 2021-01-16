package org.perpetualnetworks.mdcrawler.scrapers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawler.config.MendeleyConfiguration;
import org.perpetualnetworks.mdcrawler.converters.MendeleyArticleConverter;
import org.perpetualnetworks.mdcrawler.publishers.AwsSnsPublisher;
import org.perpetualnetworks.mdcrawler.scrapers.dto.MendeleyResponse;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

class MendeleyScraperTest {

    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient.Builder()
            .build();
    private static final AwsConfiguration AWS_CONFIG = AwsConfiguration.builder()
            .credentialsFile("config/aws.json")
            .sqsUrl("https://sqs.eu-central-1.amazonaws.com/397254617684/crawler_queue")
            .region("eu-central-1")
            .build();

    private static final AwsSnsPublisher publisher = new AwsSnsPublisher(AWS_CONFIG);

    private static final MendeleyConfiguration CONFIG = MendeleyConfiguration.builder()
            .host("data.mendeley.com")
            .endPoint("api/research-data/search")
            .searchQuery("molecular trajectories")
            .type("DATASET")
            .connectTimeoutMinutes(5)
            .writeTimeoutMinutes(5)
            .readTimeoutMinutes(5)
            .build();

    private static final MendeleyArticleConverter MENDELEY_ARTICLE_CONVERTER = new MendeleyArticleConverter();
    public static final ObjectMapper MAPPER = new ObjectMapper();

    //@Disabled("works with live data")
    @SneakyThrows
    @Test
    void queryresult() {
        MendeleyScraper scraper = new MendeleyScraper(CONFIG, MENDELEY_ARTICLE_CONVERTER, publisher);
        Response fetch = scraper.fetch(scraper.buildHttpUrl(1));
        assert fetch.body() != null;
        InputStream src = fetch.body().byteStream();
        JsonNode srcNode = MAPPER.readTree(src);
        MendeleyResponse responses = MAPPER.convertValue(srcNode, MendeleyResponse.class);
        System.out.println("responses count: " + responses.getCount() + " size: " + responses.getResults().size());
        System.out.println(MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(responses.getResults()));
    }

    @Disabled("works with live data")
    @SneakyThrows
    @Test
    void queryresultAll() {
        MendeleyScraper scraper = new MendeleyScraper(CONFIG, MENDELEY_ARTICLE_CONVERTER, publisher);
        System.out.println("starting fetchall");
        List<MendeleyResponse> responses = scraper.fetchAll();
        System.out.println("ending fetchall, size: " + responses.size());
        System.out.println("total results, size: " + responses.stream()
                .map(MendeleyResponse::getResults)
                .flatMap(List::stream)
                .collect(Collectors.toSet()).size());
    }

    @Disabled("works with live data")
    @SneakyThrows
    @Test
    void runScraper_OK() {
        MendeleyScraper scraper = new MendeleyScraper(CONFIG, MENDELEY_ARTICLE_CONVERTER, publisher);
        scraper.runScraper();
    }
}
