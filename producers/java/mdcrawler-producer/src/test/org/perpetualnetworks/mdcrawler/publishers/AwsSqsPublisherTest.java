package org.perpetualnetworks.mdcrawler.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.perpetualnetworks.mdcrawler.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawler.defaults.ArticleDefaults;
import org.perpetualnetworks.mdcrawler.services.metrics.MetricsService;
import org.perpetualnetworks.mdcrawler.utils.lzw.LZWCompressor;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Optional;

class AwsSqsPublisherTest {

    private final LZWCompressor lzwCompressor = new LZWCompressor();

    @Mock
    MetricsService metricsService;

    @Disabled
    @Test
    @SneakyThrows
    void sendMessage() {
        AwsSqsPublisher publisher = new AwsSqsPublisher(AwsConfiguration.builder()
                .sqsUrl("https://sqs.eu-central-1.amazonaws.com/397254617684/crawler_queue")
                .credentialsFile("config/aws.json")
                .region("eu-central-1")
                .build(), lzwCompressor, metricsService);
        ObjectMapper mapper = new ObjectMapper();
        Optional<SendMessageResponse> defaultArticleResponse = publisher.sendMessage(mapper.writeValueAsString(ArticleDefaults.anArticle().build()));
        System.out.println("response: " + defaultArticleResponse);
    }

    @Test
    void sendArticle() {
    }

    @Test
    void testSendMessage() {
    }
}