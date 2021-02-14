package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleAuthorRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.AuthorEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoOrderByField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoQueryField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.OpMatcher;

import java.util.ArrayList;
import java.util.List;

public class ArticleAuthorRelationDao extends BaseDao<ArticleAuthorRelationEntity, ArticleAuthorRelationDao.Query> {

    public ArticleAuthorRelationDao() {
        super(ArticleAuthorRelationEntity.class);
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {

        public Query(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
            super(fields, orderBy);
        }

        public static ArticleAuthorRelationDao.Query.QueryBuilder builder() {
            return new ArticleAuthorRelationDao.Query.QueryBuilder();
        }

        public static class QueryBuilder {
            private final List<DaoQueryField> fields = new ArrayList<>();

            private QueryBuilder() {
            }

            public ArticleAuthorRelationDao.Query.QueryBuilder withArticleAndAuthor(ArticleEntity articleEntity,
                                                                                    AuthorEntity authorEntity) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, articleEntity, "articleEntity"));
                fields.add(new DaoQueryField(OpMatcher.EQUALS, authorEntity, "authorEntity"));
                return this;
            }

            public ArticleAuthorRelationDao.Query build() {
                return new ArticleAuthorRelationDao.Query(ImmutableList.copyOf(fields), ImmutableList.of());
            }
        }
    }
}
