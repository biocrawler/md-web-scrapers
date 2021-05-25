package org.perpetualnetworks.mdcrawler.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleFileResponse;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleResponse;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class FigshareApiClient {

    public static final String DEFAULT_ORDER = "published_date";
    public static final String DEFAULT_SORT = "desc";
    public static final int DEFAULT_PAGE_SIZE = 100;
    private static String baseUrl = "https://api.figshare.com/v2";
    private static String articlesEndpoint = "/articles";
    private static String articleSearchEndpoint = "/articles/search";
    private OkHttpClient client;
    private ObjectMapper mapper;


    public FigshareApiClient(OkHttpClient client) {
        this.client = client;
        this.mapper = new ObjectMapper();
    }

    @SneakyThrows
    public List<ArticleFileResponse> fetchFilesForArticle(Integer articleId) {
        Request.Builder reqBuilder = new Request.Builder();
        reqBuilder.url(buildFileUrl(articleId));
        final Call call = client.newCall(reqBuilder.build());
        return fetch(call.execute(), new TypeReference<List<ArticleFileResponse>>() {
        }).orElseGet(Collections::emptyList);
    }

    public Set<ArticleResponse> fetchAllArticles(String searchTerm) {
        int currentPage = 1;
        List<ArticleResponse> responseArticles = fetchArticles(currentPage, searchTerm);
        Set<ArticleResponse> responses = new HashSet<>(responseArticles);
        log.info("starting fetch all for term: " + searchTerm);
        while (responseArticles.size() != 0) {
            responseArticles = fetchArticles(currentPage, searchTerm);
            responses.addAll(responseArticles);
            currentPage++;
            log.info(String.format("added %s articles for search term %s, current responseListSize: %s",
                    responseArticles.size(), searchTerm, responses.size()));
        }
        return responses;
    }

    @SneakyThrows
    List<ArticleResponse> fetchArticles(Integer pageNumber, String searchTerm) {
        final Request.Builder reqBuilder = buildArticlesRequest(searchTerm);
        reqBuilder.url(buildUrl(pageNumber));
        final Call call = client.newCall(reqBuilder.build());
        return fetch(call.execute(), new TypeReference<List<ArticleResponse>>() {
        }).orElseGet(Collections::emptyList);

    }

    @SneakyThrows
    private Request.Builder buildArticlesRequest(String searchTerm) {
        Request.Builder reqBuilder = new Request.Builder();
        JsonNode node = mapper.createObjectNode().put("search_for", searchTerm);
        final RequestBody body = RequestBody.create(MediaType.parse("application/json"), mapper.writeValueAsString(node));
        reqBuilder.post(body);
        return reqBuilder;
    }

    @SneakyThrows
    private <T> Optional<T> fetch(Response response, TypeReference<T> reference) {
        if (response.code() == 200 && response.body() != null) {
            return Optional.of(mapper.readValue(response.body().charStream(), reference));
        }
        if (response.body() != null) {
            log.error(String.format("could not fetch response with code: %s, body: %s",
                    response.code(), response.body().charStream().read()));
        }
        log.error(String.format("could not fetch response with code: %s",
                response.code()));
        return Optional.empty();
    }

    private String buildUrl(Integer pageNumber) {
        //example: https://api.figshare.com/v2/articles/search?page=1&page_size=100&order=published_date&order_direction=desc
        return baseUrl + articleSearchEndpoint + "?" + "page=" + pageNumber + "&page_size=" + FigshareApiClient.DEFAULT_PAGE_SIZE
                + "&order=" + DEFAULT_ORDER + "&order_direction=" + DEFAULT_SORT;
    }

    private String buildFileUrl(Integer articleId) {
        return baseUrl + articlesEndpoint + "/" + articleId + "/" + "files";
    }
}
