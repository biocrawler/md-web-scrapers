package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoOrderByField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoQueryField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.OpMatcher;

import java.util.ArrayList;
import java.util.List;

public class ArticleFileDao extends BaseDao<ArticleFileEntity, ArticleFileDao.Query> {

    public ArticleFileDao() {
        super(ArticleFileEntity.class);
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {

        public Query(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
            super(fields, orderBy);
        }

        public static ArticleFileDao.Query.QueryBuilder builder() {
            return new ArticleFileDao.Query.QueryBuilder();
        }

        public static class QueryBuilder {
            private final List<DaoQueryField> fields = new ArrayList<>();

            private QueryBuilder() {
            }

            public ArticleFileDao.Query.QueryBuilder withId(Integer id) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, id, "id"));
                return this;
            }

            public ArticleFileDao.Query.QueryBuilder withDigitaObjectlIdLike(String digitalObjectId) {
                fields.add(new DaoQueryField(OpMatcher.CONTAINS, digitalObjectId, "digitalObjectId"));
                return this;
            }

            public ArticleFileDao.Query.QueryBuilder withDigitaObjectlId(String digitalObjectId) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, digitalObjectId, "digitalObjectId"));
                return this;
            }

            public ArticleFileDao.Query.QueryBuilder withFileNameLike(String fileName) {
                fields.add(new DaoQueryField(OpMatcher.CONTAINS, fileName, "fileName"));
                return this;
            }

            public ArticleFileDao.Query.QueryBuilder withDownloadUrl(String downloadUrl) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, downloadUrl, "downloadUrl"));
                return this;
            }

            public ArticleFileDao.Query.QueryBuilder withSize(String size) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, size, "size"));
                return this;
            }

            public ArticleFileDao.Query build() {
                return new ArticleFileDao.Query(ImmutableList.copyOf(fields), ImmutableList.of());
            }
        }
    }
}
