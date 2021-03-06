package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoOrderByField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoQueryField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.OpMatcher;

import java.util.ArrayList;
import java.util.List;

public class KeywordDao extends BaseDao<KeywordEntity, KeywordDao.Query> {

    public KeywordDao() {
        super(KeywordEntity.class);
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {

        public Query(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
            super(fields, orderBy);
        }

        public static KeywordDao.Query.QueryBuilder builder() {
            return new KeywordDao.Query.QueryBuilder();
        }

        public static class QueryBuilder {
            private final List<DaoQueryField> fields = new ArrayList<>();

            private QueryBuilder() {
            }

            public KeywordDao.Query.QueryBuilder withId(Integer id) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, id, "id"));
                return this;
            }

            public KeywordDao.Query.QueryBuilder withWord(String word) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, word, "word"));
                return this;
            }

            public KeywordDao.Query.QueryBuilder withWordLike(String word) {
                fields.add(new DaoQueryField(OpMatcher.CONTAINS, word, "word"));
                return this;
            }

            public KeywordDao.Query build() {
                return new KeywordDao.Query(ImmutableList.copyOf(fields), ImmutableList.of());
            }
        }
    }
}
