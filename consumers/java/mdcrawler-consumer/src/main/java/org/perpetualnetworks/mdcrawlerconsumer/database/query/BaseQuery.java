package org.perpetualnetworks.mdcrawlerconsumer.database.query;

import lombok.Getter;

import java.util.List;

@Getter
public abstract class BaseQuery {

    private final List<DaoQueryField> fields;

    private final List<DaoOrderByField> orderBy;

    protected BaseQuery(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
        this.fields = fields;
        this.orderBy = orderBy;
    }
}
