package org.perpetualnetworks.mdcrawlerconsumer.database.converter;

import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleAuthorRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileKeywordRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleKeywordRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.models.ArticleFile;
import org.perpetualnetworks.mdcrawlerconsumer.models.Author;
import org.springframework.util.CollectionUtils;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityToDtoConverter {

    public Article convert(ArticleEntity articleEntity) {
        return Article.builder()
                .id(articleEntity.getId())
                .title(articleEntity.getTitle())
                .sourceUrl(articleEntity.getSourceUrl())
                .digitalObjectId(articleEntity.getDigitalObjectId())
                .description(articleEntity.getDescription())
                .parseDate(articleEntity.getParseDate().toString())
                .uploadDate(articleEntity.getUploadDate().toString())
                .createdDate(articleEntity.getCreatedDate().toString())
                .modifiedDate(articleEntity.getModifiedDate().toString())
                .referingUrl(articleEntity.getReferingUrl())
                .enriched(articleEntity.getEnriched())
                .published(articleEntity.getPublished())
                .parsed(articleEntity.getParsed())
                .additionalData(articleEntity.getAdditionalData())
                //related items
                .keywords(convertKeywords(articleEntity))
                .authors(convertAuthors(articleEntity))
                .files(convertFiles(articleEntity))
                .build();
    }

    public ArticleFile convert(ArticleFileEntity articleFileEntity) {
        return ArticleFile.builder()
                .fileName(articleFileEntity.getFileName())
                .url(articleFileEntity.getUrl())
                .downloadUrl(articleFileEntity.getDownloadUrl())
                .digitalObjectId(articleFileEntity.getDigitalObjectId())
                .fileDescription(articleFileEntity.getDescription())
                .referingUrl(articleFileEntity.getReferingUrl())
                .size(parseFileSize(articleFileEntity.getSize()))
                .keywords(convertFileKeywords(articleFileEntity))
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

    private Set<ArticleFile> convertFiles(ArticleEntity articleEntity) {
        //TODO: check if more info required from author
        return articleEntity.getFiles().stream()
                .map(fileArticleEntity -> ArticleFile.builder()
                        .digitalObjectId(fileArticleEntity.getDigitalObjectId())
                        .downloadUrl(fileArticleEntity.getDownloadUrl())
                        .fileDescription(fileArticleEntity.getDescription())
                        .fileName(fileArticleEntity.getFileName())
                        .referingUrl(fileArticleEntity.getReferingUrl())
                        .size(parseFileSize(fileArticleEntity.getSize()))
                        .url(fileArticleEntity.getUrl())
                        //related entities
                        .keywords(convertFileKeywords(fileArticleEntity))
                        .build())
                .collect(Collectors.toSet());
    }

    private String parseFileSize(Double size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        //TODO: fix calculation
        return new DecimalFormat("#,##0.#")
                .format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private Set<String> convertFileKeywords(ArticleFileEntity articleFileEntity) {
        if (CollectionUtils.isEmpty(articleFileEntity.getKeywordRelations())) {
            return Collections.emptySet();
        }
        return articleFileEntity.getKeywordRelations().stream()
                .map(ArticleFileKeywordRelationEntity::getKeywordEntity)
                .map(KeywordEntity::getWord)
                .collect(Collectors.toSet());
    }

}
