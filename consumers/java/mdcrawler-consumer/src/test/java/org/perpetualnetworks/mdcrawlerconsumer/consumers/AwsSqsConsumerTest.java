package org.perpetualnetworks.mdcrawlerconsumer.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.utils.lzw.LZwCompressor;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;
import java.util.Optional;

class AwsSqsConsumerTest {

    private final LZwCompressor lzwCompressor = new LZwCompressor();
    private final AwsSqsConsumer consumer = new AwsSqsConsumer(AwsConfiguration.builder()
            .sqsUrl("https://sqs.eu-central-1.amazonaws.com/397254617684/crawler_queue")
            .credentialsFile("config/aws.json")
            .region("eu-central-1")
            .build(), lzwCompressor);
    private final ObjectMapper mapper = new ObjectMapper();

    @Disabled("works with live data")
    @Test
    void bob() {
        final Optional<ReceiveMessageResponse> receiveMessageResponse = consumer.fetchMessages(1);
        System.out.println(receiveMessageResponse);
    }

    @Disabled("works with live data")
    @Test
    @SneakyThrows
    void fetchMessages() {
        final List<Article> articles = consumer.fetchArticles(1);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(articles));
    }
}