package org.perpetualnetworks.mdcrawlerconsumer.database.repository.relations;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleKeywordRelationDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.KeywordDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleKeywordRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.KeywordRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ArticleKeywordRelationRepository {

    public static final Database DEFAULT_DATABASE = Database.CRAWLER_CONSUMER;

    private final ArticleKeywordRelationDao articleKeywordRelationDao;
    private final SessionExecutor sessionExecutor;

    public ArticleKeywordRelationRepository(ArticleKeywordRelationDao articleKeywordRelationDao,
                                            SessionExecutor sessionExecutor) {
        this.articleKeywordRelationDao = articleKeywordRelationDao;
        this.sessionExecutor = sessionExecutor;
    }


    public void saveOrUpdate(ArticleEntity articleEntity, Set<String> keywords) {
        //for debugging
        //List<Integer> upsertedArticleAuthorRelations = new ArrayList<>();

        for (String keyword : keywords) {
            getExistingEntity(keyword).ifPresent((keywordEntity) -> {
                final ArticleKeywordRelationEntity articleKeywordRelationEntity = saveOrUpdate(articleEntity, keywordEntity);
                //upsertedArticleAuthorRelations.add(articleKeywordRelationEntity.getId());
            });
        }
        //log.info("number of upserted article author relations: " + upsertedArticleAuthorRelations.size());
    }

    public ArticleKeywordRelationEntity saveOrUpdate(ArticleEntity articleEntity, KeywordEntity keywordEntity) {
        Optional<ArticleKeywordRelationEntity> existingEntity = getExistingEntity(articleEntity, keywordEntity);

        return sessionExecutor.executeAndReturn(session -> {
            ArticleKeywordRelationEntity entityToSave = ArticleKeywordRelationEntity.builder()
                    .articleEntity(articleEntity)
                    .keywordEntity(keywordEntity)
                    .build();
            existingEntity.ifPresent(existingItem -> {
                entityToSave.setId(existingItem.getId());
                //entityToSave.setCreatedAt(existingItem.getCreatedAt());
                session.evict(existingItem);
            });
            return articleKeywordRelationDao.saveOrUpdate(entityToSave, session);
        }, DEFAULT_DATABASE);
    }

    Optional<KeywordEntity> getExistingEntity(String keyword) {
        KeywordRepository keywordRepository = new KeywordRepository(new KeywordDao(), sessionExecutor);
        final KeywordEntity keywordEntity = keywordRepository.saveOrUpdate(keyword);
        //TODO: fix
        return Optional.of(keywordEntity);
    }

    Optional<ArticleKeywordRelationEntity> getExistingEntity(ArticleEntity articleEntity, KeywordEntity keywordEntity) {
        final List<ArticleKeywordRelationEntity> articleKeywordRelationEntities = fetchArticleKeywordRelations(articleEntity, keywordEntity);
        Preconditions.checkArgument(articleKeywordRelationEntities.size() < 2,
                "fetch entity returned greater than non-single result");
        if (articleKeywordRelationEntities.size() == 1) {
            return Optional.of(articleKeywordRelationEntities.get(0));
        }
        return Optional.empty();
    }

    public List<ArticleKeywordRelationEntity> fetchArticleKeywordRelations(ArticleEntity articleEntity,
                                                                           KeywordEntity keywordEntity) {
        return sessionExecutor.executeAndReturn(session -> articleKeywordRelationDao.fetch(
                ArticleKeywordRelationDao.Query.builder()
                        .withArticleAndKeyword(articleEntity, keywordEntity)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }

}
