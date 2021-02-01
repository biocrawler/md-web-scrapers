package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.FileArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoOrderByField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoQueryField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.OpMatcher;

import java.util.ArrayList;
import java.util.List;

public class FileArticleDao extends BaseDao<FileArticleEntity, FileArticleDao.Query> {

    public FileArticleDao() {
        super(FileArticleEntity.class);
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {

        public Query(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
            super(fields, orderBy);
        }

        public static FileArticleDao.Query.QueryBuilder builder() {
            return new FileArticleDao.Query.QueryBuilder();
        }

        public static class QueryBuilder {
            private final List<DaoQueryField> fields = new ArrayList<>();

            private QueryBuilder() {
            }

            public FileArticleDao.Query.QueryBuilder withDigitaObjectlIdLike(String digitalObjectId) {
                fields.add(new DaoQueryField(OpMatcher.CONTAINS, digitalObjectId, "digitalObjectId"));
                return this;
            }

            public FileArticleDao.Query.QueryBuilder withDigitaObjectlId(String digitalObjectId) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, digitalObjectId, "digitalObjectId"));
                return this;
            }

            public FileArticleDao.Query.QueryBuilder withFileNameLike(String fileName) {
                fields.add(new DaoQueryField(OpMatcher.CONTAINS, fileName, "fileName"));
                return this;
            }

            public FileArticleDao.Query build() {
                return new FileArticleDao.Query(ImmutableList.copyOf(fields), ImmutableList.of());
            }
        }
    }
}
