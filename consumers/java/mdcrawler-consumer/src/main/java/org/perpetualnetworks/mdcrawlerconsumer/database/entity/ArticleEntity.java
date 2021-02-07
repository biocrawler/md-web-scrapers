package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import lombok.*;
import org.hibernate.annotations.Type;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;

import javax.persistence.*;

@Builder
@Data
@IdClass(ArticleEntity.class)
@Entity
@Table(name = "api_articlefile", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class ArticleEntity extends BaseEntity {
    public static final String TITLE = "title";
    public static final String SOURCE_URL = "source_url";
    public static final String DIGITAL_OBJECT_ID = "digital_object_id";
    public static final String DESCRIPTION = "description";
    public static final String PARSE_DATE = "parse_date";
    public static final String UPLOAD_DATE = "upload_date";

    public static final String REFERING_URL = "refering_url";
    public static final String ENRICHED = "enriched";
    public static final String PUBLISHED = "published";
    public static final String ADDITIONAL_DATA = "additional_data";
    @Id
    Long id;
    @Column(name = TITLE)
    String title;
    @Column(name = SOURCE_URL)
    String sourceUrl;
    @Column(name = DIGITAL_OBJECT_ID)
    String digitalObjectId;
    @Column(name = DESCRIPTION)
    String description;
    @Column(name = PARSE_DATE)
    String parseDate;
    @Column(name = UPLOAD_DATE)
    String uploadDate;
    //TODO: move to relation entities
    //FILES
    //KEYWORDS
    //@Column(name = AUTHORS)
    //String authors;
    @Column(name = REFERING_URL)
    String referingUrl;
    @Column(name = ENRICHED)
    Boolean enriched;
    @Column(name = PUBLISHED)
    Boolean published;
    @Column(name = ADDITIONAL_DATA, columnDefinition = "text")
    @Type(type = "json")
    Article.AdditionalData additionalData;

}
