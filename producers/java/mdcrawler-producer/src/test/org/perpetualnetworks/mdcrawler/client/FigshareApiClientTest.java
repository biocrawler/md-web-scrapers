package org.perpetualnetworks.mdcrawler.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleFileResponse;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleResponse;

import java.util.List;

class FigshareApiClientTest {

    @Disabled("works with live data")
    @Test
    void fetchFilesForArticle() {
        Integer testArticleId = 14588315;
        FigshareApiClient figshareApiClient = new FigshareApiClient(new OkHttpClient());
        final List<ArticleFileResponse> fetch = figshareApiClient.fetchFilesForArticle(testArticleId);
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fetch));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Disabled("works with live data")
    @Test
    void fetchForArticles() {
        String testTerm = "xtc";
        FigshareApiClient figshareApiClient = new FigshareApiClient(new OkHttpClient());
        final List<ArticleResponse> fetch = figshareApiClient.fetchArticles(1, testTerm);
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fetch));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}