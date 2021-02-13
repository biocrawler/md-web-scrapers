package org.perpetualnetworks.mdcrawlerconsumer.database.repository;

import com.google.common.base.Preconditions;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;

import java.util.List;
import java.util.Optional;

public class ArticleRepository {

    public static final Database DEFAULT_DATABASE = Database.CRAWLER_CONSUMER;
    private final ArticleDao articleDao;
    private final SessionExecutor sessionExecutor;
    private final Converter converter;

    public ArticleRepository(ArticleDao articleDao,
                             SessionExecutor sessionExecutor) {
        this.articleDao = articleDao;
        this.sessionExecutor = sessionExecutor;
        this.converter = new Converter();
    }

    public List<ArticleEntity> fetchArticle(String articleId) {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .withId(articleId)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<ArticleEntity> fetchArticlesByDoi(String digitalObjectId) {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .withDigitaObjectlId(digitalObjectId)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<ArticleEntity> fetchArticlesLikeDoi(String digitalObjectId) {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .withDigitaObjectlIdLike(digitalObjectId)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<ArticleEntity> fetchAllArticles() {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .build(),
                session), DEFAULT_DATABASE);
    }

    //Save or update from the Article object
    public void saveOrUpdate(Article article) {

        Optional<ArticleEntity> existingEntity = getExistingEntity(article);

        ArticleEntity entity = sessionExecutor.executeAndReturn(session -> {
            ArticleEntity entityToSave = converter.convert(article);
            existingEntity.ifPresent(existingAsset -> {
                entityToSave.setId(existingAsset.getId());
                //entityToSave.setCreatedAt(existingAsset.getCreatedAt());
                session.evict(existingAsset);
            });
            articleDao.saveOrUpdate(entityToSave, session);
            return entityToSave;
        }, DEFAULT_DATABASE);

        updateAllDependantsAndUpdateCache(article, entity);
    }

    // TODO: check if needed
    //boolean checkIfNeedToSaveToDatabase(Article article) {
    //    return true;
    //}


    Optional<ArticleEntity> getExistingEntity(Article article) {
        final List<ArticleEntity> fetchResult = fetchEntity(ArticleDao.Query.builder()
                .withDigitaObjectlId(article.getDigitalObjectId())
                .withTitle(article.getTitle())
                .build());
        Preconditions.checkArgument(fetchResult.size() == 1,
                "fetch returned non-single result");
        return fetchResult.stream().findFirst();
    }

    void updateAllDependantsAndUpdateCache(Article article, ArticleEntity articleEntity) {
        //TODO: implement these
        //articleFileRepository.saveOrUpdate(articleEntity.getId(), article.getFiles());
        //articleAuthorRepository.saveOrUpdate(articleEntity.getId(), article.getAuthors());
        //articleKeywordRepository.saveOrUpdate(articleEntity.getId(), article.getKeywords());
    }

    List<ArticleEntity> fetchEntity(ArticleDao.Query query) {
        return sessionExecutor
                .executeAndReturn(session -> articleDao.fetch(query, session),
                        DEFAULT_DATABASE);
    }
}
