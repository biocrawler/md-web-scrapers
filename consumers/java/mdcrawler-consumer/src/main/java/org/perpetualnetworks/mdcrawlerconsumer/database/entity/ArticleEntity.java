package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Data
@IdClass(ArticleEntity.class)
@Entity
@Table(name = "api_article", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class ArticleEntity extends BaseEntity {
    public static final String TITLE = "title";
    public static final String SOURCE_URL = "source_url";
    public static final String DIGITAL_OBJECT_ID = "digital_object_id";
    public static final String DESCRIPTION = "description";
    public static final String PARSE_DATE = "parse_date";
    public static final String UPLOAD_DATE = "upload_date";
    public static final String CREATED_DATE = "created_date";
    public static final String MODIFIED_DATE = "modified_date";

    public static final String REFERING_URL = "refering_url";
    public static final String ENRICHED = "enriched";
    public static final String PUBLISHED = "published";
    public static final String ADDITIONAL_DATA = "additional_data";
    @Id
    Long id;
    @Column(name = TITLE, columnDefinition = "text")
    String title;
    @Column(name = SOURCE_URL, columnDefinition = "text")
    String sourceUrl;
    @Column(name = DIGITAL_OBJECT_ID)
    String digitalObjectId;
    @Column(name = DESCRIPTION, columnDefinition = "text")
    String description;
    @Column(name = PARSE_DATE, columnDefinition = "datetime", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    Date parseDate;
    //TODO: relation entities for below
    //FILES
    //KEYWORDS
    //AUTHORS
    @Column(name = UPLOAD_DATE, columnDefinition = "DATETIME(6)", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    Date uploadDate;
    @Column(name = CREATED_DATE, columnDefinition = "DATETIME(6)", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    Date createdDate;
    @Column(name = MODIFIED_DATE, columnDefinition = "DATETIME(6)", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy'T'HH:mm:ss")
    Date modifiedDate;
    @Column(name = REFERING_URL)
    String referingUrl;
    @Column(name = ENRICHED)
    Boolean enriched;
    @Column(name = PUBLISHED)
    Boolean published;
    @Column(name = ADDITIONAL_DATA, columnDefinition = "text")
    @Type(type = "json")
    @ColumnDefault("{}")
    Article.AdditionalData additionalData;

}
