package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "api_articlefile", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class ArticleFileEntity {
    @Id
    Long id;
    @Column(name = "file_name")
    String fileName;
    @Column
    String url;
    @Column(name = "download_url")
    String downloadUrl;
    @Column(name = "digital_object_id")
    String digitalObjectId;
    //text
    @Column(name = "refering_url")
    String referingUrl;
    @Column(name = "size")
    Double size;
    @Column(name = "article_id")
    Integer artcileId;

}
