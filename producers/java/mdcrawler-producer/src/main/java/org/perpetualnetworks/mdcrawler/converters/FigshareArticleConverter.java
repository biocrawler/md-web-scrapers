package org.perpetualnetworks.mdcrawler.converters;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.perpetualnetworks.mdcrawler.models.Author;
import org.perpetualnetworks.mdcrawler.models.FileArticle;
import org.perpetualnetworks.mdcrawler.parsers.WebParser;
import org.perpetualnetworks.mdcrawler.services.BrowserAutomatorImpl;
import org.perpetualnetworks.mdcrawler.services.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
@Slf4j
public class FigshareArticleConverter {

    private final WebParser webParser;
    private final MetricsService metricsService;

    @Autowired
    public FigshareArticleConverter(WebParser webParser, MetricsService metricsService) {
        this.webParser = webParser;
        this.metricsService = metricsService;
    }


    public Optional<Article> buildArticleFromElement(WebElement webElement) {
        try {
            List<String> textElements = webParser.parseAllTextElements(webElement);
            Article.ArticleBuilder builder = buildArticleBuilder(textElements);
            List<String> urls = webParser.parseAllLinks(webElement);
            List<String> sourceUrls = urls.stream().filter(url -> url.startsWith("http")).collect(Collectors.toList());
            sourceUrls.stream().findFirst().ifPresent(builder::sourceUrl);
            Article article = builder.build();
            //log.info("returning article from build: " + article);
            metricsService.incrementFigshareArticleConversionSuccessCount();
            return Optional.of(article);
        } catch (Exception e) {
            log.error("error: " + e.getMessage());
            metricsService.incrementFigshareArticleConversionErrorCount();
        }
        return Optional.empty();
    }
    public Article.ArticleBuilder buildArticleBuilder(List<String> textElements) {
        Article.ArticleBuilder builder = Article.builder();
        Optional<Author> author = webParser.parseAuthor(textElements);
        Optional<String> title = webParser.parseTitle(textElements);
        webParser.parseType(textElements).ifPresent(string -> {
            builder.additionalData(Article.AdditionalData.builder().figshareType(string).build());
        });
        Optional<String> date = webParser.parsePostedDate(textElements);
        author.ifPresent(parsedAuthor -> builder.authors(Collections.singleton(parsedAuthor)));
        title.ifPresent(builder::title);
        if (title.isEmpty()) {
            log.warn("could not parse title from text: " + textElements);
        }
        date.ifPresent(builder::uploadDate);
        return builder;
    }

    //TODO: clean up
    public Article updateArticleBySecondaryLink(Article article, BrowserAutomatorImpl browserAutomator, WebParser parser) {
        log.info("starting secondary parse");
        WebDriver webDriver = buildWebDriverAndWait(article, browserAutomator);

        Article.ArticleBuilder builder = article.toBuilder();

        if (article.getAdditionalData() != null) {
            log.info("parsing article type: " + article.getAdditionalData().getFigshareType());
        }
        Set<String> keywords = parser.parseAllKeywords(browserAutomator.fetchAllFSArticleKeywordElements(webDriver));
        log.info("keyword set not empty: " + CollectionUtils.isNotEmpty(keywords));
        builder.keywords(keywords);
        Set<String> dois = parser.parseArticleDoi(browserAutomator.fetchAllFSArticleDoiElements(webDriver));
        log.info("secondary dois not empty: " + CollectionUtils.isNotEmpty(dois));
        dois.stream().filter(Objects::nonNull).findFirst().ifPresent(builder::digitalObjectId);
        Timestamp ts = new Timestamp(Instant.now().toEpochMilli());
        builder.parseDate(ts.toLocalDateTime().toString());
        List<String> abstracts = webParser.parseArticleAbstract(browserAutomator.fetchAllFSArticleAbstracts(webDriver));
        log.info("abstracts not empty: " + CollectionUtils.isNotEmpty(abstracts));
        if (CollectionUtils.isNotEmpty(abstracts)) {
            builder.description(String.join(" ", abstracts));
        }
        Set<FileArticle> fileArticles = webParser.parseArticleFiles(browserAutomator.fetchAllFSArticleFiles(webDriver));
        builder.files(fileArticles);
        log.info("files not empty: " + CollectionUtils.isNotEmpty(fileArticles) + " size: " + fileArticles.size());
        metricsService.incrementFigshareArticleWithFilesCount();
        if (fileArticles.size() == 0 && nonNull(article.getAdditionalData()) && !article.getAdditionalData().getFigshareType().equals("COLLECTION")) {
            log.warn("zero files collected from article: " + article);
        }
        webDriver.close();
        return builder.build();
    }

    @NotNull
    private WebDriver buildWebDriverAndWait(Article article, BrowserAutomatorImpl browserAutomator) {
        WebDriver driver = browserAutomator.createWebDriver();
        driver.get(article.getSourceUrl());
        browserAutomator.waitImplicity(driver, 5);
        return driver;
    }
}
