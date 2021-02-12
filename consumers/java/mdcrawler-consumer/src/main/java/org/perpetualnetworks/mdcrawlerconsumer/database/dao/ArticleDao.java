package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoOrderByField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoQueryField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.OpMatcher;

import java.util.ArrayList;
import java.util.List;

public class ArticleDao extends BaseDao<ArticleEntity, ArticleDao.Query> {

    public ArticleDao() {
        super(ArticleEntity.class);
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {

        public Query(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
            super(fields, orderBy);
        }

        public static ArticleDao.Query.QueryBuilder builder() {
            return new ArticleDao.Query.QueryBuilder();
        }

        public static class QueryBuilder {
            private final List<DaoQueryField> fields = new ArrayList<>();

            private QueryBuilder() {
            }
            public ArticleDao.Query.QueryBuilder withId(String articleId) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, articleId, "id"));
                return this;
            }

            public ArticleDao.Query.QueryBuilder withDigitaObjectlIdLike(String digitalObjectId) {
                fields.add(new DaoQueryField(OpMatcher.CONTAINS, digitalObjectId, "digitalObjectId"));
                return this;
            }

            public ArticleDao.Query.QueryBuilder withDigitaObjectlId(String digitalObjectId) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, digitalObjectId, "digitalObjectId"));
                return this;
            }

            public ArticleDao.Query.QueryBuilder withFileNameLike(String fileName) {
                fields.add(new DaoQueryField(OpMatcher.CONTAINS, fileName, "fileName"));
                return this;
            }

            public ArticleDao.Query build() {
                return new ArticleDao.Query(ImmutableList.copyOf(fields), ImmutableList.of());
            }
        }
    }
}
