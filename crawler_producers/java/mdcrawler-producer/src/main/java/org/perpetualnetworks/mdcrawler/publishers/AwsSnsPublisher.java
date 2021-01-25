package org.perpetualnetworks.mdcrawler.publishers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawler.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.perpetualnetworks.mdcrawler.utils.lzw.LZWCompressor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
public class AwsSnsPublisher {
    private final static ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    public static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
    public static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";
    private final AwsConfiguration awsConfiguration;
    private final SqsClient sqsClient;
    private final LZWCompressor compressor;
    private AwsBasicCredentials awsBasicCredentials;


    @Autowired
    public AwsSnsPublisher(AwsConfiguration awsConfiguration, LZWCompressor compressor) {
        this.awsConfiguration = awsConfiguration;
        parseAwsCredentials(awsConfiguration).ifPresent(c -> this.awsBasicCredentials = c);
        this.sqsClient = SqsClient.builder()
                .region(Region.of(awsConfiguration.getRegion()))
                .build();
        this.compressor = compressor;
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
        try{
            return sendMessage(serialized);
        } catch (Exception e) {
            log.error("could not send article: " + e.getCause());
        }
        return Optional.empty();

    }
    //TODO: setup message download for very large messages from s3
    public Optional<SendMessageResponse> sendMessage(String message) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(awsBasicCredentials.accessKeyId(), awsBasicCredentials.secretAccessKey());
        SendMessageRequest request = buildSqsRequest(credentials, message);
        try {
            return Optional.of(sqsClient.sendMessage(request));
        } catch (Exception e) {
            log.error("error sending message: ", e);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private SendMessageRequest buildSqsRequest(AwsBasicCredentials credentials, String message) {
        final String compressedMessage = compressMessage(message, compressor);
        return SendMessageRequest.builder()
                .overrideConfiguration(AwsRequestOverrideConfiguration.builder()
                        .credentialsProvider(() -> credentials)
                        .build())
                .messageBody(compressedMessage)
                .queueUrl(awsConfiguration.getSqsUrl())
                .build();
    }

    @NotNull
    private String compressMessage(String message, LZWCompressor compressor) {
        final byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        final String compressedMessage = Arrays.toString(compressor.compress(messageBytes));
        final String finalMessage = compressedMessage.strip().replaceAll(", ", " ");
        long initialSize = Stream.of(messageBytes).count();
        long finalSize = Stream.of(compressedMessage.getBytes()).count();
        log.info("message original size: " + initialSize + " message final size: " + finalSize);
        return finalMessage;
    }
}