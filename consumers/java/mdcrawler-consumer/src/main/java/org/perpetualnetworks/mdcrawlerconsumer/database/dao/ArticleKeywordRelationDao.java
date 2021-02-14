package org.perpetualnetworks.mdcrawlerconsumer.database.dao;

import com.google.common.collect.ImmutableList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleKeywordRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.BaseQuery;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoOrderByField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.DaoQueryField;
import org.perpetualnetworks.mdcrawlerconsumer.database.query.OpMatcher;

import java.util.ArrayList;
import java.util.List;

public class ArticleKeywordRelationDao extends BaseDao<ArticleKeywordRelationEntity, ArticleKeywordRelationDao.Query> {

    public ArticleKeywordRelationDao() {
        super(ArticleKeywordRelationEntity.class);
    }

    @Getter
    @EqualsAndHashCode(callSuper = true)
    public static class Query extends BaseQuery {

        public Query(List<DaoQueryField> fields, List<DaoOrderByField> orderBy) {
            super(fields, orderBy);
        }

        public static ArticleKeywordRelationDao.Query.QueryBuilder builder() {
            return new ArticleKeywordRelationDao.Query.QueryBuilder();
        }

        public static class QueryBuilder {
            private final List<DaoQueryField> fields = new ArrayList<>();

            private QueryBuilder() {
            }

            public ArticleKeywordRelationDao.Query.QueryBuilder withArticleAndKeyword(ArticleEntity articleEntity,
                                                                                      KeywordEntity keywordEntity) {
                fields.add(new DaoQueryField(OpMatcher.EQUALS, articleEntity, "articleEntity"));
                fields.add(new DaoQueryField(OpMatcher.EQUALS, keywordEntity, "keywordEntity"));
                return this;
            }

            public ArticleKeywordRelationDao.Query build() {
                return new ArticleKeywordRelationDao.Query(ImmutableList.copyOf(fields), ImmutableList.of());
            }
        }
    }
}
