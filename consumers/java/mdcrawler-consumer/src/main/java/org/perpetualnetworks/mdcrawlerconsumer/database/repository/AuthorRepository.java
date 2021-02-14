package org.perpetualnetworks.mdcrawlerconsumer.database.repository;

import com.google.common.base.Preconditions;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.converter.Converter;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.AuthorDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.AuthorEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.models.Author;

import java.util.List;
import java.util.Optional;

public class AuthorRepository {

    public static final Database DEFAULT_DATABASE = Database.CRAWLER_CONSUMER;
    private final AuthorDao authorDao;
    private final SessionExecutor sessionExecutor;
    private final Converter converter;

    public AuthorRepository(AuthorDao authorDao,
                            SessionExecutor sessionExecutor) {
        this.authorDao = authorDao;
        this.sessionExecutor = sessionExecutor;
        this.converter = new Converter();
    }

    public List<AuthorEntity> fetchAuthor(Integer authorId) {
        return sessionExecutor.executeAndReturn(session -> authorDao.fetch(
                AuthorDao.Query.builder()
                        .withId(authorId)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<AuthorEntity> fetchAuthorsEqualTo(String name) {
        return sessionExecutor.executeAndReturn(session -> authorDao.fetch(
                AuthorDao.Query.builder()
                        .withName(name)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<AuthorEntity> fetchAuthorsLike(String name) {
        return sessionExecutor.executeAndReturn(session -> authorDao.fetch(
                AuthorDao.Query.builder()
                        .withNameLike(name)
                        .build(),
                session), DEFAULT_DATABASE);
    }

    public List<AuthorEntity> fetchAllAuthors() {
        return sessionExecutor.executeAndReturn(session -> authorDao.fetch(
                AuthorDao.Query.builder()
                        .build(),
                session), DEFAULT_DATABASE);
    }

    //Save or update from the Author object
    public AuthorEntity saveOrUpdate(Author author) {
        Optional<AuthorEntity> existingEntity = getExistingEntity(author);

        AuthorEntity entity = sessionExecutor.executeAndReturn(session -> {
            AuthorEntity entityToSave = converter.convert(author);
            existingEntity.ifPresent(existingItem -> {
                entityToSave.setId(existingItem.getId());
                //entityToSave.setCreatedAt(existingItem.getCreatedAt());
                session.evict(existingItem);
            });
            return authorDao.saveOrUpdate(entityToSave, session);
        }, DEFAULT_DATABASE);

        return entity;
    }


    Optional<AuthorEntity> getExistingEntity(Author author) {
        final List<AuthorEntity> fetchResult = fetchEntity(AuthorDao.Query.builder()
                .withName(author.getName())
                .build());
        Preconditions.checkArgument(fetchResult.size() < 2,
                "fetch entity returned greater than non-single result");
        return fetchResult.stream().findFirst();
    }

    List<AuthorEntity> fetchEntity(AuthorDao.Query query) {
        return sessionExecutor
                .executeAndReturn(session -> authorDao.fetch(query, session),
                        DEFAULT_DATABASE);
    }
}
