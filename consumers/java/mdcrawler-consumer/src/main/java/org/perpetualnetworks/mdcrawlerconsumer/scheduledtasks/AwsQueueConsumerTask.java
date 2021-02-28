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

import java.util.List;

@Component
@Slf4j
public class AwsQueueConsumerTask {

    @Autowired
    SessionFactoryStoreImpl sessionFactoryStore;

    @Autowired
    AwsSqsConsumer awsSqsConsumer;

    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        log.info("starting scheduled task aws queue consume");
        SessionExecutor sessionExecutor = new SessionExecutor(sessionFactoryStore);
        ArticleRepository articleRepository = new ArticleRepository(new ArticleDao(), sessionExecutor);

        for (int i = 0; i < 100; i++) {
            try {
                final List<Article> articles = awsSqsConsumer.fetchArticles(10);
                for (Article article : articles) {
                    try {
                        Integer articleId = articleRepository.saveOrUpdate(article);
                        log.info("article saved with id: " + articleId);
                    } catch (Exception e) {
                        log.error("failed to convert article: ", e);
                    }
                }
            } catch (Exception e) {
                log.error("fetch error: ", e);
            }
            log.info("endinging scheduled task aws queue consume");
        }
    }

}
