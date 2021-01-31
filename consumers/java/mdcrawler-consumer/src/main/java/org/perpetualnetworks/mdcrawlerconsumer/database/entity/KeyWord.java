package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "api_keyword", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class KeyWord {
    @Id
    Long id;

    @Column(name = "word")
    String word;

    @Column(name = "created_date")
    String createdDate;

    @Column(name = "modified_date")
    String modifiedDate;
}
