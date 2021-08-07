package org.perpetualnetworks.mdcrawlerconsumer.database.repository;

import com.google.common.base.Preconditions;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleAuthorRelationDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleFileDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleKeywordRelationDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.relations.ArticleAuthorRelationRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.relations.ArticleKeywordRelationRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.models.ArticleFile;
import org.perpetualnetworks.mdcrawlerconsumer.models.Author;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

    public List<ArticleEntity> fetchArticlesEqualTo(String digitalObjectId) {
        return sessionExecutor.executeAndReturn(session -> articleDao.fetch(
                ArticleDao.Query.builder()
                        .withDigitaObjectlId(digitalObjectId)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<ArticleEntity> fetchArticlesLike(String digitalObjectId) {
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
    //TODO: add metrics here
    public Integer saveOrUpdate(Article article) {
        AtomicInteger articleId = new AtomicInteger();
        Optional<ArticleEntity> existingEntity = getExistingEntity(article);

        ArticleEntity entity = sessionExecutor.executeAndReturnTransactionalRW(session -> {
            ArticleEntity entityToSave = converter.convert(article);
            existingEntity.ifPresent(existingAsset -> {
                entityToSave.setId(existingAsset.getId());
                //entityToSave.setCreatedAt(existingAsset.getCreatedAt());
                session.evict(existingAsset);
            });
            final ArticleEntity newValue = articleDao.saveOrUpdate(entityToSave, session);
            System.out.println("new value for saved article: " + newValue);
            articleId.set(newValue.getId());
            return entityToSave;
        }, DEFAULT_DATABASE);

        updateAllDependants(article, entity);
        return articleId.get();
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
        Preconditions.checkArgument(fetchResult.size() < 2,
                "fetch entity returned greater than non-single result");
        return fetchResult.stream().findFirst();
    }

    void updateAllDependants(Article article, ArticleEntity articleEntity) {
        //TODO: implement these
        updateArticleFiles(articleEntity, article.getFiles());
        updateAuthorRelations(articleEntity, article.getAuthors());
        updateKeywordRelations(articleEntity, article.getKeywords());
    }

    private void updateArticleFiles(ArticleEntity articleEntity, Set<ArticleFile> files) {
        if (CollectionUtils.isEmpty(files)) {
            return;
        }
        ArticleFileRepository articleFileRepository = new ArticleFileRepository(new ArticleFileDao(), sessionExecutor);
        articleFileRepository.saveOrUpdate(articleEntity, files);
    }

    private void updateAuthorRelations(ArticleEntity articleEntity, Set<Author> authors) {
        if (CollectionUtils.isEmpty(authors)) {
            return;
        }
        ArticleAuthorRelationRepository articleAuthorRelationRepository = new ArticleAuthorRelationRepository(
                new ArticleAuthorRelationDao(), sessionExecutor);
        articleAuthorRelationRepository.saveOrUpdate(articleEntity, authors);
    }

    private void updateKeywordRelations(ArticleEntity articleEntity, Set<String> keywords) {
        if (CollectionUtils.isEmpty(keywords)) {
            return;
        }
        ArticleKeywordRelationRepository articleKeywordRelationRepository = new ArticleKeywordRelationRepository(
                new ArticleKeywordRelationDao(), sessionExecutor);
        articleKeywordRelationRepository.saveOrUpdate(articleEntity, keywords);
    }

    List<ArticleEntity> fetchEntity(ArticleDao.Query query) {
        return sessionExecutor
                .executeAndReturn(session -> articleDao.fetch(query, session),
                        DEFAULT_DATABASE);
    }
}
