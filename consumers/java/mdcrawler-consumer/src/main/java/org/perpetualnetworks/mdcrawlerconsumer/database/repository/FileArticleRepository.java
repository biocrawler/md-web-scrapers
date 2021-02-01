package org.perpetualnetworks.mdcrawlerconsumer.database.repository;

import org.perpetualnetworks.mdcrawlerconsumer.database.Database;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.FileArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.FileArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;

import java.util.List;

public class FileArticleRepository {

    private final FileArticleDao fileArticleDao;
    private final SessionExecutor sessionExecutor;

    public FileArticleRepository(FileArticleDao fileArticleDao,
                                  SessionExecutor sessionExecutor) {
        this.fileArticleDao = fileArticleDao;
        this.sessionExecutor = sessionExecutor;
    }

    public List<FileArticleEntity> fetchArticleFiles(String fileName) {
        return sessionExecutor.executeAndReturn(session -> fileArticleDao.fetch(
                FileArticleDao.Query.builder()
                        .withFileNameLike(fileName)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }
}
