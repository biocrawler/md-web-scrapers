package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import org.hibernate.Session;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.BaseEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.QueryHelper;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class BaseDao<EntityT extends BaseEntity, QueryT extends BaseQuery> {
    private final Class<EntityT> tyClass;
    private final QueryHelper<EntityT> queryHelper = new QueryHelper<>();

    public BaseDao(Class<EntityT> tyClass) {
        this.tyClass = tyClass;
    }

    public List<EntityT> fetch(QueryT query, Session session) {
        return fetch(query, null, session);
    }

    public List<EntityT> fetch(QueryT query, Integer maxResults, Session session) {
        CriteriaQuery<EntityT> criteria = queryHelper.buildCriteriaQuery(
                this.tyClass,
                query.getFields(),
                query.getOrderBy(),
                session);

        TypedQuery<EntityT> typedQuery = session.createQuery(criteria);
        if (nonNull(maxResults) && maxResults != 0) {
            typedQuery.setMaxResults(maxResults);
        }
        return typedQuery.getResultList().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

