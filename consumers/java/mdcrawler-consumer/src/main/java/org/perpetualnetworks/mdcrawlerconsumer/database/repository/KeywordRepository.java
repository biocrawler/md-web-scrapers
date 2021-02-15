package org.perpetualnetworks.mdcrawlerconsumer.database.repository;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.KeywordDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class KeywordRepository {

    public static final Database DEFAULT_DATABASE = Database.CRAWLER_CONSUMER;
    private final KeywordDao keywordDao;
    private final SessionExecutor sessionExecutor;
    private final Converter converter;

    public KeywordRepository(KeywordDao keywordDao,
                             SessionExecutor sessionExecutor) {
        this.keywordDao = keywordDao;
        this.sessionExecutor = sessionExecutor;
        this.converter = new Converter();
    }

    public List<KeywordEntity> fetchKeyword(Integer keywordId) {
        return sessionExecutor.executeAndReturn(session -> keywordDao.fetch(
                KeywordDao.Query.builder()
                        .withId(keywordId)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<KeywordEntity> fetchKeywordsEqualTo(String word) {
        return sessionExecutor.executeAndReturn(session -> keywordDao.fetch(
                KeywordDao.Query.builder()
                        .withWord(word)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<KeywordEntity> fetchKeywordLike(String word) {
        return sessionExecutor.executeAndReturn(session -> keywordDao.fetch(
                KeywordDao.Query.builder()
                        .withWordLike(word)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<KeywordEntity> fetchAllKeywords() {
        return sessionExecutor.executeAndReturn(session -> keywordDao.fetch(
                KeywordDao.Query.builder()
                        .build(),
                session), DEFAULT_DATABASE);
    }

    //Save or update from the Keyword object
    public KeywordEntity saveOrUpdate(String keyword) {
        AtomicInteger keywordId = new AtomicInteger();
        Optional<KeywordEntity> existingEntity = getExistingEntity(keyword);

        KeywordEntity entity = sessionExecutor.executeAndReturn(session -> {
            KeywordEntity entityToSave = converter.convert(keyword);
            existingEntity.ifPresent(existingItem -> {
                entityToSave.setId(existingItem.getId());
                entityToSave.setCreatedDate(existingItem.getCreatedDate());
                session.evict(existingItem);
            });
            final KeywordEntity keywordEntity = keywordDao.saveOrUpdate(entityToSave, session);
            keywordId.set(keywordEntity.getId());
            return entityToSave;
        }, DEFAULT_DATABASE);

        //TODO: fix keywordEntities return multiple from one id
        final List<KeywordEntity> keywordEntities = fetchKeyword(keywordId.get());
        Preconditions.checkArgument(keywordEntities.size() < 2);
        return keywordEntities.get(0);
    }


    Optional<KeywordEntity> getExistingEntity(String keyword) {
        final List<KeywordEntity> fetchResult = fetchEntity(KeywordDao.Query.builder()
                .withWord(keyword)
                .build());
        if (fetchResult.size() == 1 || fetchResult.size() == 0) {
            log.error("fetch entity returned greater than non-single result: " + fetchResult);
        }
        final Optional<KeywordEntity> first = fetchResult.stream().findFirst();
        if (fetchResult.size() >= 2) {
            log.info("returning first result");
        }
        return first;
    }


    List<KeywordEntity> fetchEntity(KeywordDao.Query query) {
        return sessionExecutor
                .executeAndReturn(session -> keywordDao.fetch(query, session),
                        DEFAULT_DATABASE);
    }
}
