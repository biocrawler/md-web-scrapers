package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Data
//@IdClass(ArticleAuthorRelationEntity.class)
@Entity
@Table(name = "api_article_authors", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class ArticleAuthorRelationEntity extends BaseEntity {
    public static final String ARTICLE_ID = "article_id";
    public static final String AUTHOR_ID = "author_id";

    @ManyToOne
    @JoinColumn(name = AUTHOR_ID)
    private AuthorEntity authorEntity;

    @ManyToOne
    @JoinColumn(name = ARTICLE_ID)
    private ArticleEntity articleEntity;
}
