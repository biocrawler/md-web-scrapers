package org.perpetualnetworks.mdcrawlerconsumer.services;

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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class AwsSqsService extends AbstractAwsService {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

    private final SqsClient sqsClient;
    private final LZwCompressor compressor;
    private final AwsBasicCredentials awsBasicCredentials;
    private final AwsConfiguration awsConfiguration;

    public AwsSqsService(AwsConfiguration awsConfiguration, LZwCompressor compressor) {
        super(awsConfiguration);
        this.awsBasicCredentials = getAwsBasicCredentials();
        this.awsConfiguration = awsConfiguration;
        this.sqsClient = SqsClient.builder()
                .region(Region.of(awsConfiguration.getRegion()))
                .build();
        this.compressor = compressor;
    }

    @SneakyThrows
    public List<Message> fetchMessages(Integer size) {
        try {
            ReceiveMessageRequest request = buildSqsReceiveRequest(this.awsBasicCredentials, size);
            final ReceiveMessageResponse value = sqsClient.receiveMessage(request);
            deleteMessages(this.awsBasicCredentials, value);
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
        //hmmm, what's this for? TODO: clean
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

    @SneakyThrows
    public Article parseCompressedArticle(Message message) {
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