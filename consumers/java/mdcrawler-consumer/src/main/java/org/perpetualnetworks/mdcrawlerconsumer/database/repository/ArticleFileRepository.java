package org.perpetualnetworks.mdcrawlerconsumer.database.repository;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleFileDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleFileKeywordRelationDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.KeywordDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleFileEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.relations.ArticleFileKeywordRelationRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.models.ArticleFile;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class ArticleFileRepository {

    public static final Database DEFAULT_DATABASE = Database.CRAWLER_CONSUMER;

    private final ArticleFileDao articleFileDao;
    private final SessionExecutor sessionExecutor;
    private final Converter converter;


    public ArticleFileRepository(ArticleFileDao articleFileDao,
                                 SessionExecutor sessionExecutor) {
        this.articleFileDao = articleFileDao;
        this.sessionExecutor = sessionExecutor;
        this.converter = new Converter();
    }

    //TODO: fetch using list of integer ids
    public List<ArticleFileEntity> fetchArticleFile(Integer id) {
        final List<ArticleFileEntity> articleFileEntities = sessionExecutor.executeAndReturn(session -> articleFileDao.fetch(
                ArticleFileDao.Query.builder()
                        .withId(id)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
        if (articleFileEntities.size() == 0) {
            log.error("fetch for article file with id: " + id + " had zero results");
        }
        return articleFileEntities;
    }

    public List<ArticleFileEntity> fetchArticleFiles(String fileNameLike) {
        return sessionExecutor.executeAndReturn(session -> articleFileDao.fetch(
                ArticleFileDao.Query.builder()
                        .withFileNameLike(fileNameLike)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }

    public List<ArticleFileEntity> fetchAllArticleFiles() {
        return sessionExecutor.executeAndReturn(session -> articleFileDao.fetch(
                ArticleFileDao.Query.builder()
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }

    public void saveOrUpdate(ArticleEntity articleEntity, Set<ArticleFile> articleFiles) {
        //for debugging
        //List<Integer> upsertedArticleFiles = new ArrayList<>();
        for (ArticleFile articleFile : articleFiles) {
            final ArticleFileEntity articleFileEntity = saveOrUpdate(articleEntity, articleFile);
            //upsertedArticleFiles.add(articleFileEntity.getId());
        }
        //log.info("number of upserted article files: " + upsertedArticleFiles.size());
    }

    public ArticleFileEntity saveOrUpdate(ArticleEntity articleEntity, ArticleFile articleFile) {
        Optional<ArticleFileEntity> existingEntity = getExistingEntity(articleFile);

        ArticleFileEntity articleFileEntity = sessionExecutor.executeAndReturnTransactionalRW(session -> {
            ArticleFileEntity entityToSave = converter.convert(articleFile);
            entityToSave.setArticleEntity(articleEntity);
            existingEntity.ifPresent(existingItem -> {
                entityToSave.setId(existingItem.getId());
                //entityToSave.setCreatedAt(existingItem.getCreatedAt());
                session.evict(existingItem);
            });
            return articleFileDao.saveOrUpdate(entityToSave, session);
        }, DEFAULT_DATABASE);

        if (!CollectionUtils.isEmpty(articleFile.getKeywords())) {
            updateAllDependants(articleFile.getKeywords(), articleFileEntity);
        }
        return articleFileEntity;
    }

    void updateAllDependants(Set<String> keywords, ArticleFileEntity articleFileEntity) {
        //TODO: implement these
        //create articlefile keyword relation entity
        KeywordRepository keywordRepository = new KeywordRepository(new KeywordDao(), sessionExecutor);
        ArticleFileKeywordRelationRepository articleFileKeywordRelationRepository = new
                ArticleFileKeywordRelationRepository(new ArticleFileKeywordRelationDao(), sessionExecutor);

        final List<KeywordEntity> keywordEntities = keywords.stream()
                .map(keywordRepository::saveOrUpdate)
                .collect(Collectors.toList());

        for (KeywordEntity keywordEntity : keywordEntities) {
            articleFileKeywordRelationRepository.saveOrUpdate(articleFileEntity, keywordEntity);
        }
    }


    Optional<ArticleFileEntity> getExistingEntity(ArticleFile articleFile) {
        final List<ArticleFileEntity> fetchResult = fetchEntity(ArticleFileDao.Query.builder()
                .withFileNameLike(articleFile.getFileName())
                .withDownloadUrl(articleFile.getDownloadUrl())
                .build());
        Preconditions.checkArgument(fetchResult.size() < 2,
                "fetch entity returned greater than non-single result");
        return fetchResult.stream().findFirst();
    }

    List<ArticleFileEntity> fetchEntity(ArticleFileDao.Query query) {
        return sessionExecutor
                .executeAndReturn(session -> articleFileDao.fetch(query, session),
                        DEFAULT_DATABASE);
    }
}
