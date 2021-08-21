package org.perpetualnetworks.mdcrawlerconsumer.presentation;

import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    Converter converter;

    @GetMapping("/{id}")
    public Article findById(@PathVariable long id) {
        return articleRepository.fetchArticle(String.valueOf(id))
                .map(converter::convert)
                .orElseThrow(() -> new RuntimeException());
    }

    @GetMapping("/")
    public Collection<Article> findArticles() {
        return articleRepository.fetchAllArticles().stream()
                .map(converter::convert).collect(Collectors.toList());
    }

    // @PutMapping("/{id}")
    // @ResponseStatus(HttpStatus.OK)
    // public Article updateBook(
    //         @PathVariable("id") final String id, @RequestBody final Book book) {
    //     return book;
    // }
}
