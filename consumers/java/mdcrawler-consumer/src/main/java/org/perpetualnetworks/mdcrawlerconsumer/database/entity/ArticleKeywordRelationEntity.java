package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Data
@IdClass(ArticleKeywordRelationEntity.class)
@Entity
@Table(name = "api_article_keywordss", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class ArticleKeywordRelationEntity extends BaseEntity implements RelationEntity<Article> {
    public static final String ARTICLE_ID = "article_id";
    public static final String KEYWORD_ID = "keyword_id";
    @Id
    Integer id;
    @Column(name = KEYWORD_ID)
    String keywordId;
    @Column(name = ARTICLE_ID)
    Integer articleId;

    @Override
    public Integer getForeignKeyId() {
        return getArticleId();
    }
}
