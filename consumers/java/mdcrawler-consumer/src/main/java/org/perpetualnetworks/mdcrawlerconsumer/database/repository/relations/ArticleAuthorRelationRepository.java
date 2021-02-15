package org.perpetualnetworks.mdcrawlerconsumer.database.repository.relations;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleAuthorRelationDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.AuthorDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleAuthorRelationEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.AuthorEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.AuthorRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.models.Author;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class ArticleAuthorRelationRepository {

    public static final Database DEFAULT_DATABASE = Database.CRAWLER_CONSUMER;

    private final ArticleAuthorRelationDao articleAuthorRelationDao;
    private final SessionExecutor sessionExecutor;

    public ArticleAuthorRelationRepository(ArticleAuthorRelationDao articleAuthorRelationDao,
                                           SessionExecutor sessionExecutor) {
        this.articleAuthorRelationDao = articleAuthorRelationDao;
        this.sessionExecutor = sessionExecutor;
    }

    public List<ArticleAuthorRelationEntity> fetchArticleAuthorRelations(ArticleEntity articleEntity,
                                                                         AuthorEntity authorEntity) {
        return sessionExecutor.executeAndReturn(session -> articleAuthorRelationDao.fetch(
                ArticleAuthorRelationDao.Query.builder()
                        .withArticleAndAuthor(articleEntity, authorEntity)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }

    public void saveOrUpdate(ArticleEntity articleEntity, Set<Author> authors) {
        //for debgging
        //List<Integer> upsertedArticleAuthorRelations = new ArrayList<>();

        for (Author author : authors) {
            getExistingEntity(author).ifPresent((authorEntity) -> {
                final ArticleAuthorRelationEntity articleAuthorRelationEntity = saveOrUpdate(articleEntity, authorEntity);
                //upsertedArticleAuthorRelations.add(articleAuthorRelationEntity.getId());
            });
        }
        //log.info("upserted article author relations " + upsertedArticleAuthorRelations.size());
    }

    public ArticleAuthorRelationEntity saveOrUpdate(ArticleEntity articleEntity, AuthorEntity authorEntity) {
        Optional<ArticleAuthorRelationEntity> existingEntity = getExistingEntity(articleEntity, authorEntity);

        return sessionExecutor.executeAndReturn(session -> {
            ArticleAuthorRelationEntity entityToSave = ArticleAuthorRelationEntity.builder()
                    .articleEntity(articleEntity)
                    .authorEntity(authorEntity)
                    .build();
            existingEntity.ifPresent(existingItem -> {
                entityToSave.setId(existingItem.getId());
                //entityToSave.setCreatedAt(existingItem.getCreatedAt());
                session.evict(existingItem);
            });
            //for debugging
            //log.info("article author relation entity" + articleAuthorRelationEntity);
            return articleAuthorRelationDao.saveOrUpdate(entityToSave, session);
        }, DEFAULT_DATABASE);

    }

    Optional<AuthorEntity> getExistingEntity(Author author) {
        AuthorRepository authorRepository = new AuthorRepository(new AuthorDao(), sessionExecutor);
        final AuthorEntity authorEntity = authorRepository.saveOrUpdate(author);
        //TODO: fix
        return Optional.of(authorEntity);
    }

    Optional<ArticleAuthorRelationEntity> getExistingEntity(ArticleEntity articleEntity, AuthorEntity authorEntity) {
        final List<ArticleAuthorRelationEntity> authorRelationEntities = fetchArticleAuthorRelations(articleEntity, authorEntity);
        Preconditions.checkArgument(authorRelationEntities.size() < 2);
        if (authorRelationEntities.size() == 1) {
            return Optional.of(authorRelationEntities.get(0));
        }
        return Optional.empty();
    }

}
