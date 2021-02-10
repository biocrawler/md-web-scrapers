package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Builder
@Data
@IdClass(FileArticleEntity.class)
@Entity
@Table(name = "api_articlefile", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class FileArticleEntity extends BaseEntity {
    public static final String FILE_NAME = "file_name";
    public static final String DOWNLOAD_URL = "download_url";
    public static final String URL = "url";
    public static final String DESCRIPTION = "description";
    public static final String DIGITAL_OBJECT_ID = "digital_object_id";
    public static final String REFERING_URL = "refering_url";
    public static final String SIZE = "size";
    public static final String ARTICLE_ID = "article_id";
    @Id
    @GeneratedValue
    Integer id;
    @Column(name = FILE_NAME)
    String fileName;
    @Column(name = URL)
    String url;
    @Column(name = DOWNLOAD_URL)
    String downloadUrl;
    @Column(name = DIGITAL_OBJECT_ID)
    String digitalObjectId;
    @Column(name = DESCRIPTION, columnDefinition = "text")
    String description;
    @Column(name = REFERING_URL)
    String referingUrl;
    @Column(name = SIZE)
    Double size;
    //@Column(name = ARTICLE_ID)
    //Integer articleId;
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "asset_id", nullable = false)
    // ArticleEntity article;

    //@OneToMany(mappedBy = "articleFileId")
    //Set<FileKeywordRelationEntity> keywordRelations;


}
