package org.perpetualnetworks.mdcrawlerconsumer.consumers;

import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.services.AwsSqsService;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class AwsSqsConsumer {

    public static final int DEFAULT_MESSAGE_PARSER_THREADS = 10;
    private final AwsSqsService awsSqsService;
    private final ForkJoinPool forkJoinPool;
    public static final float MAX_MESSAGE_SIZE = 10.0f;

    public AwsSqsConsumer(AwsSqsService awsSqsService) {
        this.awsSqsService = awsSqsService;
        this.forkJoinPool = new ForkJoinPool(DEFAULT_MESSAGE_PARSER_THREADS);
    }

    public List<Article> fetchArticles(Integer size) {
        final double pages = Math.ceil(size / MAX_MESSAGE_SIZE);
        final List<Message> messages = IntStream.range(0, (int) pages)
                .mapToObj(page -> forkJoinPool.submit(() -> awsSqsService.fetchMessages((int) MAX_MESSAGE_SIZE)))
                .map(ForkJoinTask::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return messages
                    .stream()
                    .map(awsSqsService::parseCompressedArticle)
                    .collect(Collectors.toList());
    }

}