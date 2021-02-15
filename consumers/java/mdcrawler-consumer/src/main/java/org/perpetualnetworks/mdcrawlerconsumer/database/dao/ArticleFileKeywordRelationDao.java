package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileKeywordRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoOrderByField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoQueryField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.OpMatcher;

import java.util.ArrayList;
import java.util.List;

public class ArticleFileKeywordRelationDao extends BaseDao<ArticleFileKeywordRelationEntity, ArticleFileKeywordRelationDao.Query> {

    public ArticleFileKeywordRelationDao() {
        super(ArticleFileKeywordRelationEntity.class);
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {

        public Query(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
            super(fields, orderBy);
        }

        public static ArticleFileKeywordRelationDao.Query.QueryBuilder builder() {
            return new ArticleFileKeywordRelationDao.Query.QueryBuilder();
        }

        public static class QueryBuilder {
            private final List<DaoQueryField> fields = new ArrayList<>();

            private QueryBuilder() {
            }

            public ArticleFileKeywordRelationDao.Query.QueryBuilder withArticleFileAndKeyword(ArticleFileEntity articleFileEntity,
                                                                                              KeywordEntity keywordEntity) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, articleFileEntity, "articleFileEntity"));
                fields.add(new DaoQueryField(OpMatcher.EQUALS, keywordEntity, "keywordEntity"));
                return this;
            }

            public ArticleFileKeywordRelationDao.Query build() {
                return new ArticleFileKeywordRelationDao.Query(ImmutableList.copyOf(fields), ImmutableList.of());
            }
        }
    }
}
