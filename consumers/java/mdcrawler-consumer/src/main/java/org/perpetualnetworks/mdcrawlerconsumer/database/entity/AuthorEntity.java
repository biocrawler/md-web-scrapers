package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "api_author", schema = Constants.DatabaseSchema.CRAWLER_CONSUMER)
public class AuthorEntity extends BaseEntity {
    @Id
    Long id;

    @Column(name = "name")
    String name;
}
