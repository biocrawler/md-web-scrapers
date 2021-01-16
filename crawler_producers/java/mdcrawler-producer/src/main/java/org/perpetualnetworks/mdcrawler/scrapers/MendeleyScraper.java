package org.perpetualnetworks.mdcrawler.scrapers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.perpetualnetworks.mdcrawler.config.MendeleyConfiguration;
import org.perpetualnetworks.mdcrawler.converters.MendeleyArticleConverter;
import org.perpetualnetworks.mdcrawler.publishers.AwsSnsPublisher;
import org.perpetualnetworks.mdcrawler.scrapers.dto.MendeleyResponse;
import org.perpetualnetworks.mdcrawler.utils.ParallelService;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@Slf4j
public class MendeleyScraper {

    private final OkHttpClient client;
    private final MendeleyConfiguration mendeleyConfiguration;
    private final MendeleyArticleConverter mendeleyArticleConverter;
    private final AwsSnsPublisher publisher;
    private final ParallelService parallelService;
    private final ObjectMapper mapper;

    public MendeleyScraper(MendeleyConfiguration mendeleyConfiguration,
                           MendeleyArticleConverter mendeleyArticleConverter,
                           AwsSnsPublisher publisher) {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(mendeleyConfiguration.getConnectTimeoutMinutes(), TimeUnit.MINUTES)
                .writeTimeout(mendeleyConfiguration.getWriteTimeoutMinutes(), TimeUnit.MINUTES)
                .readTimeout(mendeleyConfiguration.getReadTimeoutMinutes(), TimeUnit.MINUTES)
                .build();
        this.mendeleyConfiguration = mendeleyConfiguration;
        this.mendeleyArticleConverter = mendeleyArticleConverter;
        this.parallelService = new ParallelService(4);
        this.publisher = publisher;
        this.mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SneakyThrows
    public Response fetch(HttpUrl httpUrl) {
        Request request = new Request.Builder().url(httpUrl).build();
        return client.newCall(request).execute();
    }

    public HttpUrl buildHttpUrl(Integer pageNumber) {
        return new HttpUrl.Builder()
                .scheme("https")
                .host(mendeleyConfiguration.getHost())
                .addPathSegments(mendeleyConfiguration.getEndPoint())
                .addQueryParameter("search", mendeleyConfiguration.getSearchQuery())
                .addQueryParameter("type", mendeleyConfiguration.getType())
                .addQueryParameter("page", String.valueOf(pageNumber))
                .build();
    }

    @SneakyThrows
    public Optional<MendeleyResponse> convertResponse(Response response) {
        try (ResponseBody body = response.body();) {
            if (body != null) {
                InputStream src = body.byteStream();
                JsonNode srcNode = mapper.readTree(src);
                //log.info("attempting to convert stream of: \n" + srcNode);
                MendeleyResponse mendeleyResponse = mapper
                        .convertValue(srcNode, MendeleyResponse.class);
                response.close();
                return Optional.of(mendeleyResponse);
            }
        } catch (Exception e) {
            log.error("exception during response conversion", e);
        }
        return Optional.empty();
    }

    public List<MendeleyResponse> fetchAll() {
        List<MendeleyResponse> responses = new ArrayList<>();
        convertResponse(fetch(buildHttpUrl(1)))
                .ifPresent(response -> responses.addAll(fetchRemaining(response)));
        return responses;
    }

    private Set<MendeleyResponse> fetchRemaining(MendeleyResponse response) {
        Set<MendeleyResponse> responses = new HashSet<>();
        Integer count = response.getCount();
        int size = response.getResults() != null ? response.getResults().size() : 10;
        log.info("count: " + count + " size: " + size);
        responses.add(response);
        double pages = Math.ceil((double) count / size);
        log.info("pages found: " + pages);
        responses.addAll(parallelService.executeAndReturnParallelAndEventAware(this::fetchPage,
                IntStream.range(2, (int) pages).boxed().collect(Collectors.toList())));
        return responses;
    }

    private List<MendeleyResponse> fetchPage(Stream<Integer> pagesStream) {
        return pagesStream
                .map(page -> convertResponse(fetch(buildHttpUrl(page))))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public void runScraper() {
        fetchAll().stream()
                .map(MendeleyResponse::getResults)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(mendeleyArticleConverter::convert)
                .forEach(article -> {
                    try {
                        publisher.sendArticle(article);
                    }catch (Exception e) {
                        log.error("article could not be sent: ", e.getCause());
                    }
                });
    }

}
