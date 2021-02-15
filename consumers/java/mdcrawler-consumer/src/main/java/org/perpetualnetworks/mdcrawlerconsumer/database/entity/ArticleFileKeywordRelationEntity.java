package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@IdClass(ArticleFileKeywordRelationEntity.class)
@Entity
@Table(name = "api_articlefile_keywords", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class ArticleFileKeywordRelationEntity extends BaseEntity {
    public static final String ARTICLEFILE_ID = "articlefile_id";
    public static final String KEYWORD_ID = "keyword_id";

    @ManyToOne
    @JoinColumn(name = ARTICLEFILE_ID)
    private ArticleFileEntity articleFileEntity;

    @Getter
    @ManyToOne
    @JoinColumn(name = KEYWORD_ID)
    private KeywordEntity keywordEntity;

}
