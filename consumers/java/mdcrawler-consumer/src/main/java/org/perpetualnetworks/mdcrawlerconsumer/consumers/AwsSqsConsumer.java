package org.perpetualnetworks.mdcrawlerconsumer.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.config.AwsConfiguration;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.utils.ByteOperations;
import org.perpetualnetworks.mdcrawlerconsumer.utils.lzw.LZwCompressor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.awscore.AwsRequestOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class AwsSqsConsumer {
    private static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
    private static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    private final AwsConfiguration awsConfiguration;
    private final SqsClient sqsClient;
    private final LZwCompressor compressor;
    private AwsBasicCredentials awsBasicCredentials;

    public AwsSqsConsumer(AwsConfiguration awsConfiguration, LZwCompressor compressor) {
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
    public Optional<ReceiveMessageResponse> fetchMessages(Integer size) {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(awsBasicCredentials.accessKeyId(), awsBasicCredentials.secretAccessKey());
            ReceiveMessageRequest request = buildSqsRequest(credentials, size);
            return Optional.of(sqsClient.receiveMessage(request));
        } catch (Exception e) {
            log.error("error sending message: ", e.getCause());
        }
        return Optional.empty();

    }

    private ReceiveMessageRequest buildSqsRequest(AwsBasicCredentials credentials, Integer size) {
        //final String compressedMessage = compressMessage(message, compressor);
        return ReceiveMessageRequest.builder()
                .overrideConfiguration(AwsRequestOverrideConfiguration.builder()
                        .credentialsProvider(() -> credentials)
                        .build())
                .maxNumberOfMessages(size)
                .queueUrl(awsConfiguration.getSqsUrl())
                .build();
    }

    public List<Article> fetchArticles(Integer size) {
        List<Article> articles = new ArrayList<>();
        fetchMessages(size)
                .ifPresent(response -> articles.addAll(parseResponseMessages(response)));
        return articles;
    }

    @NotNull
    private List<Article> parseResponseMessages(ReceiveMessageResponse response) {
        return response.messages()
                .stream()
                .map(this::parseCompressedArticle)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private Article parseCompressedArticle(Message message) {
        final String processedArticleString = Arrays.stream(
                message.body().split(","))
                .filter(Objects::nonNull)
                .map(ByteOperations::convertCompressedStringToBytes)
                .map(compressor::decompress)
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .collect(Collectors.joining(" "));
        final Article article = MAPPER.readValue(processedArticleString, Article.class);
        return article.toBuilder().keywords(article.getKeywords()
                .stream()
                .map(ByteOperations::convertStringBytesToString)
                .collect(Collectors.toSet())).build();
    }
}
