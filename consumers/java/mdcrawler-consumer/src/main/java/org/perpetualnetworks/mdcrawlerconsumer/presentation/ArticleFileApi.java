package org.perpetualnetworks.mdcrawlerconsumer.presentation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleFileRepository;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.models.ArticleFile;
import org.springdoc.core.converters.models.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/article-files")
@Slf4j
public class ArticleFileApi {

    @Autowired
    ArticleFileRepository articleFileRepository;

    @Autowired
    Converter converter;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Article",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Article.class))})})
    @GetMapping("/{id}")
    public ArticleFile findById(@PathVariable long id) {
        return articleFileRepository.fetchArticleFile((int) id)
                .map(converter::convert)
                .orElseThrow(RuntimeException::new);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article List",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))})})
    @GetMapping("/")
    public Page<ArticleFile> findArticles(@RequestParam(required = false, defaultValue = "1") Integer page,
                                      @RequestParam(required = false, defaultValue = "20") Integer size,
                                      @RequestParam(required = false, defaultValue = "asc") String sort) {
        final List<ArticleFileEntity> articleFileEntities = articleFileRepository.fetchAllArticleFiles(new Pageable(page, size, List.of(sort)));
        final List<ArticleFile> collect = articleFileEntities.stream()
                .map(converter::convert)
                .collect(Collectors.toList());
        return Page.<ArticleFile>builder().size(size).content(collect).build();
    }

}
