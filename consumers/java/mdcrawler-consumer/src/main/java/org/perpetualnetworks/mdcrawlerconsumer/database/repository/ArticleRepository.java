package org.perpetualnetworks.mdcrawlerconsumer.database.repository;

import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;

import java.util.List;

public class ArticleRepository {

    private final ArticleDao articleDao;
    private final SessionExecutor sessionExecutor;

    public ArticleRepository(ArticleDao articleDao,
                             SessionExecutor sessionExecutor) {
        this.articleDao = articleDao;
        this.sessionExecutor = sessionExecutor;
    }
    public List<ArticleEntity> fetchArticle(String articleId) {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .withId(articleId)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }

    public List<ArticleEntity> fetchArticlesByDoi(String digitalObjectId) {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .withDigitaObjectlId(digitalObjectId)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }

    public List<ArticleEntity> fetchArticlesLikeDoi(String digitalObjectId) {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .withDigitaObjectlIdLike(digitalObjectId)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }

    public List<ArticleEntity> fetchAllArticles() {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }
}
