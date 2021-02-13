package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@Data
@IdClass(FileKeywordRelationEntity.class)
@Entity
@Table(name = "api_articlefile_keywords", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class FileKeywordRelationEntity extends BaseEntity {
    public static final String ARTICLEFILE_ID = "articlefile_id";
    public static final String KEYWORD_ID = "keyword_id";

    @ManyToOne
    @JoinColumn(name = ARTICLEFILE_ID)
    private FileArticleEntity articleFileEntity;

    @ManyToOne
    @JoinColumn(name = KEYWORD_ID)
    private KeywordEntity keywordEntity;

}
