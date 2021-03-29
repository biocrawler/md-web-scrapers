package org.perpetualnetworks.mdcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.perpetualnetworks.mdcrawler.config.FigshareConfiguration;
import org.perpetualnetworks.mdcrawler.converters.FigshareArticleConverter;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.perpetualnetworks.mdcrawler.parsers.WebParser;
import org.perpetualnetworks.mdcrawler.publishers.AwsSqsPublisher;
import org.perpetualnetworks.mdcrawler.services.BrowserAutomatorImpl;
import org.perpetualnetworks.mdcrawler.services.metrics.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class FigshareScraper {
    private final FigshareConfiguration figshareConfiguration;
    private final BrowserAutomatorImpl browserAutomator;
    private final FigshareArticleConverter figshareArticleConverter;
    private final AwsSqsPublisher publisher;
    private final MetricsService metricsService;
    private final WebParser webParser;

    @Autowired
    public FigshareScraper(FigshareConfiguration figshareConfiguration,
                           BrowserAutomatorImpl browserAutomator,
                           FigshareArticleConverter figshareArticleConverter,
                           AwsSqsPublisher publisher,
                           MetricsService metricsService) {
        this.figshareConfiguration = figshareConfiguration;
        this.browserAutomator = browserAutomator;
        this.figshareArticleConverter = figshareArticleConverter;
        this.publisher = publisher;
        this.webParser = new WebParser();
        this.metricsService = metricsService;

    }

    public void runScraper() {
        //Debugging
        log.info("finished waiting, going to page: " + figshareConfiguration.getQueryUrl());
        log.info("starting get");
        startQuery();
        waitForArticles();
        agreeToCookies();

        log.info("fetching initial webelements");
        List<WebElement> initialWebElements = browserAutomator.buildPageArticleElements();
        log.info("starting batching for new articles");


        final int initialArticleCount = 0;
        fetchAndSendArticlesByBatch(browserAutomator, initialWebElements, initialArticleCount);

        log.info("finished batching for articles");
        log.info("closing webdriver");
        browserAutomator.closeWebDriver();
    }

    private void agreeToCookies() {
        log.info("agreeing to cookies");
        browserAutomator.agreeToCookies(browserAutomator.getWebDriver());
    }

    private void waitForArticles() {
        log.info("waiting for article divs");
        String articleSelector = "div[role=article]";
        browserAutomator.waitByCssSelector(browserAutomator.getWebDriver(), articleSelector);
    }

    private void startQuery() {
        browserAutomator.getWebDriver().get(figshareConfiguration.getQueryUrl());
    }

    private void fetchAndSendArticlesByBatch(BrowserAutomatorImpl browserAutomator,
                                             List<WebElement> existingWebElementList, int articleCount) {
        if (isFetchLimitHit(articleCount)) {
            return;
        }
        log.info("starting new batch article fetch, article count: " + articleCount);

        // TODO: build pattern around logic:
        // is the existing element list the same as the what is seen on the page?
        // if so, then scroll down until the existing element list is not the same
        // then publish the converted articles

        final Set<String> existingElementListText = existingWebElementList.stream()
                .map(WebElement::getText).collect(Collectors.toSet());

        boolean isSame = true;
        List<WebElement> elements = new ArrayList<>();
        while (isSame) {
            elements = browserAutomator.buildPageArticleElements();
            if (elements.stream().map(WebElement::getText).collect(Collectors.toSet()).equals(existingElementListText)) {
                log.info("same check: " + isSame + " executing manual scroll");
                browserAutomator.executeManualScrollDown();
            }
            isSame = false;
        }
        log.info("elements no longer the same exiting loop, collecting articles");

        Set<Article> currentArticles = buildArticles(browserAutomator, elements);

        log.info("starting article publish");
        publishArticles(currentArticles);
        metricsService.incrementFigshareArticleBatchCount();

        articleCount += currentArticles.size();
        fetchAndSendArticlesByBatch(browserAutomator, elements, articleCount);
    }

    private Set<Article> buildArticles(BrowserAutomatorImpl browserAutomator, List<WebElement> existingList) {
        Set<Article> articles = new HashSet<>();
        final WebDriver webDriver = browserAutomator.getWebDriver();
        browserAutomator.waitImplicity(webDriver, 5);

        existingList.forEach(element -> {
            Optional<Article> article = figshareArticleConverter.buildArticleFromElement(element);

            article.ifPresent(value -> articles.add(
                    figshareArticleConverter.updateArticleBySecondaryLink(value, browserAutomator, webParser)));
        });

        browserAutomator.waitImplicity(webDriver, 5);
        webDriver.close();
        return articles;
    }

    private boolean isFetchLimitHit(int articleCount) {
        if (articleCount > figshareConfiguration.getFetchLimit()) {
            log.info("fetch limit exceeded");
            return true;
        }
        return false;
    }

    private void publishArticles(Set<Article> articles) {
        List<SendMessageResponse> sendResponses = articles.stream()
                .map(publisher::sendArticle)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
        log.info("send message responses: " + sendResponses);

        metricsService.sumFigshareArticleSendSum(sendResponses.size());
    }

}
