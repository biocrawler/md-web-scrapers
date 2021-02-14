package org.perpetualnetworks.mdcrawlerconsumer.database.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.AuthorEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.models.ArticleFile;
import org.perpetualnetworks.mdcrawlerconsumer.models.Author;

public class Converter {

    final ObjectMapper mapper = new ObjectMapper();
    private final EntityToDtoConverter entityToDtoConverter;
    private final DtoToEntityConverter dtoToEntityConverter;

    public Converter() {
        this.dtoToEntityConverter = new DtoToEntityConverter();
        this.entityToDtoConverter = new EntityToDtoConverter();
    }

    public ArticleEntity convert(Article article) {
        return dtoToEntityConverter.convert(article);
    }

    public Article convert(ArticleEntity articleEntity) {
        return entityToDtoConverter.convert(articleEntity);
    }

    public ArticleFileEntity convert(ArticleFile articleFile) {
        return dtoToEntityConverter.convert(articleFile);
    }

    public ArticleFile convert(ArticleFileEntity articleFileEntity) {
        return entityToDtoConverter.convert(articleFileEntity);
    }

    public KeywordEntity convert(String keyword) {
        return dtoToEntityConverter.convert(keyword);
    }

    public AuthorEntity convert(Author author) {
        return dtoToEntityConverter.convert(author);
    }
}