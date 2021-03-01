package org.perpetualnetworks.mdcrawlerconsumer.database.repository.relations;

import com.google.common.base.Preconditions;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleFileKeywordRelationDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileKeywordRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;

import java.util.List;
import java.util.Optional;

public class ArticleFileKeywordRelationRepository {

    public static final Database DEFAULT_DATABASE = Database.CRAWLER_CONSUMER;

    private final ArticleFileKeywordRelationDao articleFileKeywordRelationDao;
    private final SessionExecutor sessionExecutor;

    public ArticleFileKeywordRelationRepository(ArticleFileKeywordRelationDao articleFileKeywordRelationDao,
                                                SessionExecutor sessionExecutor) {
        this.articleFileKeywordRelationDao = articleFileKeywordRelationDao;
        this.sessionExecutor = sessionExecutor;
    }

    public ArticleFileKeywordRelationEntity saveOrUpdate(ArticleFileEntity articleFileEntity, KeywordEntity keywordEntity) {
        Optional<ArticleFileKeywordRelationEntity> existingEntity = getExistingEntity(articleFileEntity, keywordEntity);

        return sessionExecutor.executeAndReturnTransactionalRW(session -> {
            ArticleFileKeywordRelationEntity entityToSave = ArticleFileKeywordRelationEntity.builder()
                    .articleFileEntity(articleFileEntity)
                    .keywordEntity(keywordEntity)
                    .build();
            existingEntity.ifPresent(existingAsset -> {
                entityToSave.setId(existingAsset.getId());
                //entityToSave.setCreatedAt(existingAsset.getCreatedAt());
                session.evict(existingAsset);
            });
            return articleFileKeywordRelationDao.saveOrUpdate(entityToSave, session);
        }, DEFAULT_DATABASE);
    }

    Optional<ArticleFileKeywordRelationEntity> getExistingEntity(ArticleFileEntity articleFileEntity, KeywordEntity keywordEntity) {
        final List<ArticleFileKeywordRelationEntity> fetchResult = fetchArticleFileKeywordRelations(articleFileEntity, keywordEntity);
        Preconditions.checkArgument(fetchResult.size() < 2,
                "fetch entity returned greater than non-single result");
        return fetchResult.stream().findFirst();
    }

    public List<ArticleFileKeywordRelationEntity> fetchArticleFileKeywordRelations(ArticleFileEntity articleFileEntity,
                                                                                   KeywordEntity keywordEntity) {
        return sessionExecutor.executeAndReturn(session -> articleFileKeywordRelationDao.fetch(
                ArticleFileKeywordRelationDao.Query.builder()
                        .withArticleFileAndKeyword(articleFileEntity, keywordEntity)
                        .build(), session), DEFAULT_DATABASE);
    }
}
