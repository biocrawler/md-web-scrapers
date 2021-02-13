package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Type;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Data
//@IdClass(ArticleEntity.class)
@Entity
@Table(name = "api_article", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class ArticleEntity extends BaseEntity {
    public static final String TITLE = "title";
    public static final String SOURCE_URL = "source_url";
    public static final String DIGITAL_OBJECT_ID = "digital_object_id";
    public static final String DESCRIPTION = "description";
    public static final String PARSE_DATE = "parse_date";
    public static final String PARSED = "parsed";
    public static final String UPLOAD_DATE = "upload_date";
    public static final String CREATED_DATE = "created_date";
    public static final String MODIFIED_DATE = "modified_date";

    public static final String REFERING_URL = "refering_url";
    public static final String ENRICHED = "enriched";
    public static final String PUBLISHED = "published";
    public static final String ADDITIONAL_DATA = "additional_data";

    @Column(name = TITLE, columnDefinition = "text")
    String title;
    @Column(name = SOURCE_URL, columnDefinition = "text")
    String sourceUrl;
    @Column(name = DIGITAL_OBJECT_ID)
    String digitalObjectId;
    @Column(name = REFERING_URL)
    String referingUrl;
    @Column(name = DESCRIPTION, columnDefinition = "text")
    String description;
    @Column(name = PARSE_DATE, columnDefinition = "datetime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.Time.IsoPattern)
    Date parseDate;
    @Column(name = UPLOAD_DATE, columnDefinition = "DATETIME(6)", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.Time.IsoPattern)
    Date uploadDate;
    @Column(name = PARSED)
    Boolean parsed;
    @Column(name = ENRICHED)
    Boolean enriched;
    @Column(name = PUBLISHED)
    Boolean published;
    @Column(name = CREATED_DATE, columnDefinition = "DATETIME(6)", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.Time.IsoPattern)
    Date createdDate;
    @Column(name = MODIFIED_DATE, columnDefinition = "DATETIME(6)", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.Time.IsoPattern)
    Date modifiedDate;
    @Column(name = ADDITIONAL_DATA, columnDefinition = "text")
    @Type(type = "json")
    @ColumnDefault("{}")
    Article.AdditionalData additionalData;

    @OneToMany(mappedBy = "articleEntity", fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ArticleKeywordRelationEntity> keywordRelations = new ArrayList<>();

    @OneToMany(mappedBy = "articleEntity", fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<ArticleAuthorRelationEntity> authorRelations = new ArrayList<>();

    @OneToMany(mappedBy = "articleEntity", fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<FileArticleEntity> files = new ArrayList<>();

    //KEYWORDS
    //AUTHORS

}
