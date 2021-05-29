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
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public List<Message> fetchMessages(Integer size) {
        try {
            AwsBasicCredentials credentials = AwsBasicCredentials.create(awsBasicCredentials.accessKeyId(), awsBasicCredentials.secretAccessKey());
            ReceiveMessageRequest request = buildSqsReceiveRequest(credentials, size);
            final ReceiveMessageResponse value = sqsClient.receiveMessage(request);
            deleteMessages(credentials, value);
            return value.messages();
        } catch (Exception e) {
            log.error("error receieving message: " + e.getMessage());
        }
        return Collections.emptyList();

    }

    private void deleteMessages(AwsBasicCredentials credentials, ReceiveMessageResponse value) {
        try {
            final List<DeleteMessageBatchRequestEntry> batchRequestEntries = value.messages()
                    .stream()
                    .map(this::buildSqsDeleteRequest)
                    .collect(Collectors.toList());

            final DeleteMessageBatchRequest deleteMessageBatchRequest = buildSqsDeleteBatchRequest(credentials, batchRequestEntries);
            sqsClient.deleteMessageBatch(deleteMessageBatchRequest);
        } catch (Exception e) {
            log.error("unable to delete message batch for response: " + value);
        }
    }

    private ReceiveMessageRequest buildSqsReceiveRequest(AwsBasicCredentials credentials, Integer size) {
        //final String compressedMessage = compressMessage(message, compressor);
        return ReceiveMessageRequest.builder()
                .overrideConfiguration(AwsRequestOverrideConfiguration.builder()
                        .credentialsProvider(() -> credentials)
                        .build())
                .maxNumberOfMessages(size)
                .queueUrl(awsConfiguration.getSqsUrl())
                .build();
    }

    private DeleteMessageBatchRequestEntry buildSqsDeleteRequest(Message message) {
        return DeleteMessageBatchRequestEntry.builder()
                .receiptHandle(message.receiptHandle())
                .id(message.messageId())
                .build();
    }

    private DeleteMessageBatchRequest buildSqsDeleteBatchRequest(AwsBasicCredentials credentials, Collection<DeleteMessageBatchRequestEntry> entries) {
        return DeleteMessageBatchRequest.builder()
                .overrideConfiguration(AwsRequestOverrideConfiguration.builder()
                        .credentialsProvider(() -> credentials)
                        .build())
                .queueUrl(awsConfiguration.getSqsUrl())
                .entries(entries)
                .build();
    }


    public List<Article> fetchArticles(Integer size) {
        final List<Message> messages = fetchMessages(size);
        return parseResponseMessages(messages);
    }

    @NotNull
    private List<Article> parseResponseMessages(List<Message> messages) {
        return messages
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
        return article.toBuilder().keywords(decodeKeywords(article.getKeywords())).build();
    }

    @Nonnull
    private Set<String> decodeKeywords(Set<String> articleKeywords) {
        if (articleKeywords == null) {
            return Collections.emptySet();
        }
        Set<String> keywords = new HashSet<>();
        articleKeywords.forEach(word -> {
            Pattern normalPattern = Pattern.compile("^[a-zA-Z]+");
            Matcher normalMatcher = normalPattern.matcher(word);
            if (normalMatcher.matches()) {
                keywords.add(word);
                return;
            }
            Pattern byteCodePattern = Pattern.compile("^[\\[0-9,\\s]{2,}+]$");
            Matcher byteCodeMatcher = byteCodePattern.matcher(word);
            if (byteCodeMatcher.matches()) {
                keywords.add(ByteOperations.convertStringBytesToString(word));
                return;
            }
            log.warn("no matching pattern for keyword string: " + word + " adding by default");
            keywords.add(word);
        });
        return keywords;
    }
}
