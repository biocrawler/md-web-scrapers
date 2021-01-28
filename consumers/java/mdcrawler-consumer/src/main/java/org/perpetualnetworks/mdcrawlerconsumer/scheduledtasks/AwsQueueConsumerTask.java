package org.perpetualnetworks.mdcrawlerconsumer.scheduledtasks;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AwsQueueConsumerTask {

    @Scheduled(fixedRate = 60*1000)
    public void run() {
        log.info("starting scheduled task aws queue consume");
        log.info("endinging scheduled task aws queue consume");
    }

}
