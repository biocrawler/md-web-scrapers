package org.perpetualnetworks.mdcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawler.client.FigshareApiClient;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleFileResponse;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleResponse;
import org.perpetualnetworks.mdcrawler.config.FigshareApiConfiguration;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.perpetualnetworks.mdcrawler.models.FileArticle;
import org.perpetualnetworks.mdcrawler.publishers.AwsSqsPublisher;
import org.perpetualnetworks.mdcrawler.services.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@Slf4j
public class FigshareApiScraper {

    private final String defaultSearchTerms;
    private final String defaultDateFormat;

    private final FigshareApiClient figshareApiClient;
    private final AwsSqsPublisher publisher;
    private final MetricsService metricsService;

    @Autowired
    FigshareApiScraper(FigshareApiClient figshareApiClient,
                       FigshareApiConfiguration figshareApiConfiguration,
                       AwsSqsPublisher publisher,
                       MetricsService metricsService
    ) {
        this.figshareApiClient = figshareApiClient;
        this.publisher = publisher;
        this.metricsService = metricsService;
        this.defaultSearchTerms = figshareApiConfiguration.getSearchTerms();
        this.defaultDateFormat = figshareApiConfiguration.getDateFormat();
    }

    public void runScraper() {
        log.info("starting build all articles");
        Set<Article> currentArticles = buildArticles();

        log.info("starting article publish");
        publishArticles(currentArticles);
        log.info("finished article publish");
    }


    private void publishArticles(Set<Article> articles) {
        List<SendMessageResponse> sendResponses = articles.stream()
                .map(publisher::sendArticle)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        log.info("sent message with responses size: " + sendResponses.size());

        metricsService.sumFigshareArticleSendSum(sendResponses.size());
    }

    public Set<ArticleResponse> fetchArticlesForDefaultTerms() {
        Set<ArticleResponse> allResponses = new HashSet<>();
        for (String term : defaultSearchTerms.split(",")) {
            allResponses.addAll(figshareApiClient.fetchAllArticles(term));
        }
        return allResponses;
    }

    public Set<Article> buildArticles() {
        Set<ArticleResponse> articleResponses = fetchArticlesForDefaultTerms();
        return articleResponses.stream().map(ar -> {
            Article.ArticleBuilder builder = Article.builder()
                    .title(ar.getTitle())
                    .sourceUrl(ar.getUrl())
                    //TODO: add keywords .keywords()
                    .digitalObjectId(ar.getDoi())
                    .parsed(true)
                    .parseDate(getFormattedDate())
                    .referingUrl(ar.getUrlPrivateHtml());
            addAdditionalData(ar, builder);
            fetchArticleFiles(ar, builder);
            return builder.build();
        }).collect(Collectors.toSet());
    }

    private String getFormattedDate() {
        return new SimpleDateFormat(defaultDateFormat).format(new Date());
    }

    private void fetchArticleFiles(ArticleResponse ar, Article.ArticleBuilder builder) {
        try {
            final List<ArticleFileResponse> articleFileResponses = figshareApiClient
                    .fetchFilesForArticle(Long.parseLong(String.valueOf(ar.getId())));
            final Set<FileArticle> files = articleFileResponses.stream().map(af -> FileArticle.builder()
                    .downloadUrl(af.getDownloadUrl())
                    .size(af.getSize().toString())
                    .fileName(af.getFileName())
                    .build()).collect(Collectors.toSet());
            builder.files(files);
            log.info("added " + files.size() + " to article with doi: " + ar.getDoi());
        } catch (Exception e) {
            log.error("could not add article files: ", e);
        }
    }

    private void addAdditionalData(ArticleResponse ar, Article.ArticleBuilder builder) {
        if (nonNull(ar.getDefinedType())) {
            builder.additionalData(Article.AdditionalData.builder()
                    .figshareType(ar.getDefinedTypeName())
                    .build());
        }
    }


}
