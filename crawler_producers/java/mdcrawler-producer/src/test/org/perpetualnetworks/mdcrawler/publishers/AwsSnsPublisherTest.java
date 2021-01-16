package org.perpetualnetworks.mdcrawler.publishers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawler.defaults.ArticleDefaults;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Optional;

class AwsSnsPublisherTest {

    @Disabled
    @Test
    @SneakyThrows
    void sendMessage() {
        AwsSnsPublisher publisher = new AwsSnsPublisher(AwsConfiguration.builder()
                .sqsUrl("https://sqs.eu-central-1.amazonaws.com/397254617684/crawler_queue")
                .credentialsFile("config/aws.json")
                .region("eu-central-1")
                .build());
        ObjectMapper mapper = new ObjectMapper();
        Optional<SendMessageResponse> bob = publisher.sendMessage(mapper.writeValueAsString(ArticleDefaults.anArticle().build()));
        System.out.println("response: " + bob);
    }

    @Test
    void sendArticle() {
    }

    @Test
    void testSendMessage() {
    }
}