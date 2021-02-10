package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Data
@IdClass(FileKeywordRelationEntity.class)
@Entity
@Table(name = "api_articlefile_keywords", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class FileKeywordRelationEntity extends BaseEntity implements RelationEntity<Article> {
    public static final String ARTICLEFILE_ID = "articlefile_id";
    public static final String KEYWORD_ID = "keyword_id";
    @Id
    @GeneratedValue
    Integer id;
    // @ManyToOne
    // @JoinColumn(name = KEYWORD_ID)
    // KeyWordEntity keyword;
    // @ManyToOne
    // @JoinColumn(name = ARTICLEFILE_ID)
    // FileArticleEntity fileArticle;

    @Override
    public Integer getForeignKeyId() {
        return null;//getFileArticle().getId();
    }
}
