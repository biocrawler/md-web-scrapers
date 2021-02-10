package org.perpetualnetworks.mdcrawlerconsumer.database.entity;

public interface RelationEntity<RelationTypeT> {

    Integer getId();

    Integer getForeignKeyId();

    default RelationTypeT getRelationType() {
        return null;
    }
}
