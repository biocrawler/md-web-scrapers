package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.AuthorEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoOrderByField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoQueryField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.OpMatcher;

import java.util.ArrayList;
import java.util.List;

public class AuthorDao extends BaseDao<AuthorEntity, AuthorDao.Query> {

    public AuthorDao() {
        super(AuthorEntity.class);
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {

        public Query(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
            super(fields, orderBy);
        }

        public static AuthorDao.Query.QueryBuilder builder() {
            return new AuthorDao.Query.QueryBuilder();
        }

        public static class QueryBuilder {
            private final List<DaoQueryField> fields = new ArrayList<>();

            private QueryBuilder() {
            }

            public AuthorDao.Query.QueryBuilder withId(Integer id) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, id, "id"));
                return this;
            }

            public AuthorDao.Query.QueryBuilder withName(String name) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, name, "name"));
                return this;
            }

            public AuthorDao.Query.QueryBuilder withNameLike(String name) {
                fields.add(new DaoQueryField(OpMatcher.CONTAINS, name, "name"));
                return this;
            }

            public AuthorDao.Query build() {
                return new AuthorDao.Query(ImmutableList.copyOf(fields), ImmutableList.of());
            }
        }
    }
}
