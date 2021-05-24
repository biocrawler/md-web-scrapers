package org.perpetualnetworks.mdcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawler.client.FigshareApiClient;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleFileResponse;
import org.perpetualnetworks.mdcrawler.client.dto.figshare.ArticleResponse;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.perpetualnetworks.mdcrawler.models.FileArticle;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
public class FigshareApiScraper {

    public static final String DEFAULT_SEARCH_TERMS = "xtc,dcd,ntraj,netcdf,trr,lammpstrj,xyz,binpos,hdf5,dtr,arc,tng,mdcrd,crd,dms,trj,ent,ncdf";
    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("YYYY-mm-dd'T'hh:mm:ss'Z'");

    private FigshareApiClient figshareApiClient;

    FigshareApiScraper(FigshareApiClient figshareApiClient) {
        this.figshareApiClient = figshareApiClient;
    }


    public Set<ArticleResponse> fetchArticlesForDefaultTerms() {
        Set<ArticleResponse> allResponses = new HashSet<>();
        for (String term : DEFAULT_SEARCH_TERMS.split(",")) {
            allResponses.addAll(figshareApiClient.fetchAllArticles(term));
        }
        return allResponses;
    }

    public Set<Article> fetchAllUpdatedArticlesForDefaultTerms() {
        Set<ArticleResponse> articleResponses = fetchArticlesForDefaultTerms();
        return articleResponses.stream().map(ar -> {
            Article.ArticleBuilder builder = Article.builder()
                    .title(ar.getTitle())
                    .sourceUrl(ar.getUrl())
                    //TODO: add keywords .keywords()
                    .digitalObjectId(ar.getDoi())
                    .parsed(true)
                    .parseDate(DEFAULT_DATE_FORMAT.format(new Date()))
                    .referingUrl(ar.getUrlPrivateHtml());
            addAdditionalData(ar, builder);
            fetchArticleFiles(ar, builder);
            return builder.build();
        }).collect(Collectors.toSet());
    }

    private void fetchArticleFiles(ArticleResponse ar, Article.ArticleBuilder builder) {
        try {
            final List<ArticleFileResponse> articleFileResponses = figshareApiClient
                    .fetchFilesForArticle(ar.getId());
            builder.files(articleFileResponses.stream().map(af -> FileArticle.builder()
                    .downloadUrl(af.getDownloadUrl())
                    .size(af.getSize().toString())
                    .fileName(af.getFileName())
                    .build()).collect(Collectors.toSet()));
        } catch (Exception e) {
            log.error("could not add article files: " + e.getMessage());
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
