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

    public List<FileArticleEntity> fetchArticleFiles(String fileNameLike) {
        return sessionExecutor.executeAndReturn(session -> fileArticleDao.fetch(
                FileArticleDao.Query.builder()
                        .withFileNameLike(fileNameLike)
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }

    public List<FileArticleEntity> fetchAllArticleFiles() {
        return sessionExecutor.executeAndReturn(session -> fileArticleDao.fetch(
                FileArticleDao.Query.builder()
                        .build(),
                session), Database.CRAWLER_CONSUMER);
    }
}
