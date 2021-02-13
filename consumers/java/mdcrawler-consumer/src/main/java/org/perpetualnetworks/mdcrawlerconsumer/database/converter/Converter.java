package org.perpetualnetworks.mdcrawlerconsumer.database.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleAuthorRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleKeywordRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.FileArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.FileKeywordRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.models.Author;
import org.perpetualnetworks.mdcrawlerconsumer.models.FileArticle;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public class Converter {

    final ObjectMapper mapper = new ObjectMapper();

    public ArticleEntity convert(Article article) {
        return ArticleEntity.builder()
                .title(article.getTitle())
                .sourceUrl(article.getSourceUrl())
                .digitalObjectId(article.getDigitalObjectId())
                .description(article.getDescription())
                .parseDate(parseTime(article.getParseDate()))
                .uploadDate(parseTime(article.getUploadDate()))
                .referingUrl(article.getReferingUrl())
                .enriched(article.getEnriched())
                .published(article.getPublished())
                .additionalData(article.getAdditionalData())
                //related items
                //TODO: convert related entities
                //.keywords(convertKeywords(articleEntity))
                //.authors(convertAuthors(articleEntity))
                //.files(convertFiles(articleEntity))
                .build();
    }


    public Article convert(ArticleEntity articleEntity) {
        return Article.builder()
                .title(articleEntity.getTitle())
                .sourceUrl(articleEntity.getSourceUrl())
                .digitalObjectId(articleEntity.getDigitalObjectId())
                .description(articleEntity.getDescription())
                .parseDate(articleEntity.getParseDate().toString())
                .uploadDate(articleEntity.getUploadDate().toString())
                .referingUrl(articleEntity.getReferingUrl())
                .enriched(articleEntity.getEnriched())
                .published(articleEntity.getPublished())
                .additionalData(articleEntity.getAdditionalData())
                //related items
                .keywords(convertKeywords(articleEntity))
                .authors(convertAuthors(articleEntity))
                .files(convertFiles(articleEntity))
                .build();
    }

    private Set<String> convertKeywords(ArticleEntity articleEntity) {
        //TODO: check modified, updated date required
        if (CollectionUtils.isEmpty(articleEntity.getAuthorRelations())) {
            return Collections.emptySet();
        }
        return articleEntity.getKeywordRelations().stream()
                .map(ArticleKeywordRelationEntity::getKeywordEntity)
                .map(KeywordEntity::getWord)
                .collect(Collectors.toSet());
    }

    private Set<Author> convertAuthors(ArticleEntity articleEntity) {
        //TODO: check if more info required from author
        return articleEntity
                .getAuthorRelations().stream()
                .map(ArticleAuthorRelationEntity::getAuthorEntity)
                .map(entity -> Author.builder().name(entity.getName()).build())
                .collect(Collectors.toSet());
    }

    private Set<FileArticle> convertFiles(ArticleEntity articleEntity) {
        //TODO: check if more info required from author
        return articleEntity.getFiles().stream()
                .map(fileArticleEntity -> FileArticle.builder()
                        .digitalObjectId(fileArticleEntity.getDigitalObjectId())
                        .downloadUrl(fileArticleEntity.getDownloadUrl())
                        .fileDescription(fileArticleEntity.getDescription())
                        .fileName(fileArticleEntity.getFileName())
                        .referingUrl(fileArticleEntity.getReferingUrl())
                        .size(String.valueOf(fileArticleEntity.getSize()))
                        .url(fileArticleEntity.getUrl())
                        //related entities
                        .keywords(convertFileKeywords(fileArticleEntity))
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<String> convertFileKeywords(FileArticleEntity fileArticleEntity) {
        if (CollectionUtils.isEmpty(fileArticleEntity.getKeywordRelations())) {
            return Collections.emptySet();
        }
        return fileArticleEntity.getKeywordRelations().stream()
                .map(FileKeywordRelationEntity::getKeywordEntity)
                .map(KeywordEntity::getWord)
                .collect(Collectors.toSet());
    }

    @SneakyThrows
    private Date parseTime(String dateString) {
        return new SimpleDateFormat(Constants.Time.IsoPattern).parse(dateString);
    }

}
