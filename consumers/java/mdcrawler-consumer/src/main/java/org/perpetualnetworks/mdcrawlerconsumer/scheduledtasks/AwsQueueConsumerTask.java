package org.perpetualnetworks.mdcrawlerconsumer.scheduledtasks;

import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.consumers.AwsSqsConsumer;
import org.perpetualnetworks.mdcrawlerconsumer.database.dao.ArticleDao;
import org.perpetualnetworks.mdcrawlerconsumer.database.repository.ArticleRepository;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionExecutor;
import org.perpetualnetworks.mdcrawlerconsumer.database.session.SessionFactoryStoreImpl;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AwsQueueConsumerTask {

    public static final int MINUTE_INTERVAL = 60 * 1000;
    @Autowired
    SessionFactoryStoreImpl sessionFactoryStore;

    @Autowired
    AwsSqsConsumer awsSqsConsumer;

    @Scheduled(fixedRate = MINUTE_INTERVAL)
    public void run() {
        log.info("starting scheduled task aws queue consume");
        SessionExecutor sessionExecutor = new SessionExecutor(sessionFactoryStore);
        ArticleRepository articleRepository = new ArticleRepository(new ArticleDao(), sessionExecutor);

        try {
            for (Article article : awsSqsConsumer.fetchArticles(1000)) {
                try {
                    articleRepository.saveOrUpdate(article);
                } catch (Exception e) {
                    log.error("failed to convert article: ", e);
                }
            }
        } catch (Exception e) {
            log.error("fetch articles from sqs consumer error: ", e);
        }
    }

}
