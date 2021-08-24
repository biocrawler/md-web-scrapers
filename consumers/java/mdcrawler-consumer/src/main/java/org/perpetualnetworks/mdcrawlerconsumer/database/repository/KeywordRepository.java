package org.perpetualnetworks.mdcrawlerconsumer.database.repository;

import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.KeywordDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.KeywordEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.springdoc.core.converters.models.Pageable;

import java.util.List;
import java.util.Optional;

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

    public Optional<KeywordEntity> fetchKeyword(Integer keywordId) {
        return sessionExecutor.executeAndReturn(session -> {
            final List<KeywordEntity> fetch = keywordDao.fetch(
                    KeywordDao.Query.builder()
                            .withId(keywordId)
                            .build(),
                    session);
            return fetch.stream().findFirst();
        }, DEFAULT_DATABASE);
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

    public List<KeywordEntity> fetchAllKeywords(Pageable pageable) {
        final Long count = (Long) sessionExecutor.executeAndReturn(session -> session
                .createQuery("SELECT count(*) from KeywordEntity").uniqueResult(), DEFAULT_DATABASE);
        return sessionExecutor.executeAndReturn(session ->
                        keywordDao.fetch(
                                KeywordDao.Query.builder()
                                        .build(), pageable, session),
                DEFAULT_DATABASE);
    }

    //Save or update from the Keyword object
    public KeywordEntity saveOrUpdate(String keyword) {
        Optional<KeywordEntity> existingEntity = getExistingEntity(keyword);

        KeywordEntity entity = sessionExecutor.executeAndReturnTransactionalRW(session -> {
            KeywordEntity entityToSave = converter.convert(keyword);
            existingEntity.ifPresent(existingItem -> {
                entityToSave.setId(existingItem.getId());
                entityToSave.setCreatedDate(existingItem.getCreatedDate());
                session.evict(existingItem);
            });
            return keywordDao.saveOrUpdate(entityToSave, session);
        }, DEFAULT_DATABASE);

        return entity;
    }

    public KeywordEntity updateWord(KeywordEntity keyword, String word) {
        KeywordEntity entity = sessionExecutor.executeAndReturnTransactionalRW(session -> {
            KeywordEntity entityToSave = keyword.toBuilder().word(word).build();
            session.evict(keyword);
            return keywordDao.saveOrUpdate(entityToSave, session);
        }, DEFAULT_DATABASE);

        return entity;
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
