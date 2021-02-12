package org.perpetualnetworks.mdcrawlerconsumer.scheduledtasks;

import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.entity.ArticleEntity;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStoreImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AwsQueueConsumerTask {

    @Autowired
    SessionFactoryStoreImpl sessionFactoryStore;

    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        log.info("starting scheduled task aws queue consume");
        SessionExecutor sessionExecutor = new SessionExecutor(sessionFactoryStore);
        ArticleRepository articleRepository = new ArticleRepository(new ArticleDao(), sessionExecutor);
        final List<ArticleEntity> articleEntities = articleRepository.fetchAllArticles();
        log.info(String.valueOf(articleEntities.stream().findAny().get()));
        log.info("size: " + articleEntities.size());

        log.info("endinging scheduled task aws queue consume");
    }

}
