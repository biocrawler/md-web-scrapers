package org.perpetualnetworks.mdcrawler.publishers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawler.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.io.File;
import java.util.Optional;

@Component
@Slf4j
public class AwsSnsPublisher {
    private final static ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    public static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
    public static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";
    private final AwsConfiguration awsConfiguration;
    private AwsBasicCredentials awsBasicCredentials;
    private final SqsClient sqsClient;

    @Autowired
    public AwsSnsPublisher(AwsConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;
        parseAwsCredentials(awsConfiguration).ifPresent(c -> this.awsBasicCredentials = c);
        this.sqsClient = SqsClient.builder()
                .region(Region.of(awsConfiguration.getRegion()))
                .build();
    }

    @SneakyThrows
    private Optional<AwsBasicCredentials> parseAwsCredentials(AwsConfiguration awsConfiguration) {
        File src = new File(awsConfiguration.getCredentialsFile());
        JsonNode fileJson = MAPPER.readValue(src, JsonNode.class);
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(fileJson.get(AWS_ACCESS_KEY_ID).asText(),
                fileJson.get(AWS_SECRET_ACCESS_KEY).asText());
        return Optional.of(awsBasicCredentials);
    }

    @SneakyThrows
    public Optional<SendMessageResponse> sendArticle(Article article) {
        String serialized = MAPPER.writeValueAsString(article);
        try {
            return sendMessage(serialized);
        } catch (Exception e) {
            log.error("could not send article", e.getCause());
        }
        return Optional.empty();

    }

    public Optional<SendMessageResponse> sendMessage(String message) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsBasicCredentials.accessKeyId(), awsBasicCredentials.secretAccessKey());
        SendMessageRequest request = SendMessageRequest.builder()
                .overrideConfiguration(AwsRequestOverrideConfiguration.builder()
                        .credentialsProvider(() -> credentials)
                        .build())
                .messageBody(message)
                .queueUrl(awsConfiguration.getSqsUrl())
                .build();
        try {
            return Optional.of(sqsClient.sendMessage(request));
        } catch (Exception e) {
            log.error("error sending message:", e.getCause());
        }
        return Optional.empty();
    }
}