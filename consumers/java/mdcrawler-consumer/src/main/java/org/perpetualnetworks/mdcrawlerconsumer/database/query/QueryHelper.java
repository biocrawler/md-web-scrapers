package org.perpetualnetworks.mdcrawlerconsumer.database.query;

import com.google.common.collect.ImmutableList;
import org.hibernate.Session;

import javax.persistence.criteria.*;
import java.util.*;

import static java.util.Objects.nonNull;

public class QueryHelper<T> {

    private Predicate generateSingleClause(CriteriaBuilder criteriaBuilder,
                                           Root<T> root,
                                           DaoQueryField queryField) {
        switch (queryField.getOpMatcher()) {
            case EQUALS: {
                return criteriaBuilder.equal(root.get(queryField.getFieldName()), queryField.getValue());
            }
            case CONTAINS: {
                String searchPattern = "%" + queryField.getValue() + "%";
                return criteriaBuilder.like(root.get(queryField.getFieldName()), searchPattern);
            }
            case GREATER: {
                return criteriaBuilder.greaterThan(root.get(queryField.getFieldName()), (Comparable) queryField.getValue());
            }
            case LESS: {
                return criteriaBuilder.lessThan(root.get(queryField.getFieldName()), (Comparable) queryField.getValue());
            }
            case GE: {
                return criteriaBuilder.greaterThanOrEqualTo(root.get(queryField.getFieldName()), (Comparable) queryField.getValue());
            }
            case LE: {
                return criteriaBuilder.lessThanOrEqualTo(root.get(queryField.getFieldName()), (Comparable) queryField.getValue());
            }
            default: {
                return criteriaBuilder.isTrue(criteriaBuilder.literal(false));
            }
        }
    }

    private Predicate buildClause(List<DaoQueryField> queryFieldList,
                                  CriteriaBuilder criteriaBuilder,
                                  Root<T> root) {
        return criteriaBuilder.and(queryFieldList.stream()
                .map(field -> generateSingleClause(criteriaBuilder, root, field))
                .distinct().toArray(Predicate[]::new));
    }

    public CriteriaQuery<T> buildCriteriaQuery(Class<T> tyClass,
                                               List<DaoQueryField> fields,
                                               List<DaoOrderByField> orderBy,
                                               Session session) {
        CriteriaQuery<T> criteria = session.getCriteriaBuilder().createQuery(tyClass);
        Root<T> root = criteria.from(tyClass);

        return criteria.where(
                buildClause(fields, session.getCriteriaBuilder(), root))
                .orderBy(getOrderBy(orderBy, root, session.getCriteriaBuilder()));
    }

    private List<Order> getOrderBy(List<DaoOrderByField> orderBy,
                                   Root<T> root,
                                   CriteriaBuilder builder) {
        if (nonNull(orderBy)) {
            return orderBy.stream()
                    .map(m -> getOrderBy(m.getField(), m.getResultOrder(), root, builder))
                    .collect(ImmutableList.toImmutableList());
        }
        return Collections.emptyList();
    }

    private Order getOrderBy(String field,
                             ResultOrder resultOrderBy,
                             Root<T> root,
                             CriteriaBuilder builder) {
        return resultOrderBy.equals(ResultOrder.ASC)
                ? builder.asc(root.get(field)) : builder.desc(root.get(field));
    }

}
