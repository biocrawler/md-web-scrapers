package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Data
//@IdClass(ArticleKeywordRelationEntity.class)
@Entity
@Table(name = "api_article_keywords", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class ArticleKeywordRelationEntity extends BaseEntity implements RelationEntity<Article> {
    public static final String ARTICLE_ID = "article_id";
    public static final String KEYWORD_ID = "keyword_id";
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    Integer id;
    //@Column(name = KEYWORD_ID)
    //String keywordId;
    //@Column(name = ARTICLE_ID)
    //Integer articleId;
    @ManyToOne
    @JoinColumn(name = KEYWORD_ID)
    private KeywordEntity keywordEntity;

    @ManyToOne
    @JoinColumn(name = ARTICLE_ID)
    private ArticleEntity articleEntity;

    @Override
    public Integer getForeignKeyId() {
        return articleEntity.getId();
    }
}
