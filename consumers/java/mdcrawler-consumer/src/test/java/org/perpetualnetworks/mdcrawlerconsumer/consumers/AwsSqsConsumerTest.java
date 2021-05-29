package org.perpetualnetworks.mdcrawlerconsumer.consumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.utils.lzw.LZwCompressor;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final List<Message> receiveMessageResponse = consumer.fetchMessages(1);
        System.out.println(receiveMessageResponse);
    }

    @Disabled("works with live data")
    @Test
    @SneakyThrows
    void fetchMessages() {
        final List<Article> articles = consumer.fetchArticles(1);
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(articles));
    }

    @Test
    void decodeKeywords() {
        String testString = "[76, 105, 98, 114, 97, 114, 121, 32, 97, 110, 100, 32, 73, 110, 102, 111, 114, 109, 97, 116, 105, 111, 110, 32, 83, 99, 105, 101, 110, 99, 101]";
        Pattern normalPattern = Pattern.compile("^[a-zA-Z]+");
        Matcher normalMatcher = normalPattern.matcher(testString);
        assertFalse(normalMatcher.matches());
        Pattern byteCodePattern = Pattern.compile("^[\\[0-9,\\s]{2,}+]$");
        Matcher byteCodeMatcher = byteCodePattern.matcher(testString);
        assertTrue(byteCodeMatcher.matches());
        System.out.println(byteCodeMatcher.groupCount());
        System.out.println(byteCodeMatcher.group());
    }

}