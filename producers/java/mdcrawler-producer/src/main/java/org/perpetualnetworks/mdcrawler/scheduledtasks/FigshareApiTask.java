package org.perpetualnetworks.mdcrawler.scheduledtasks;

import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawler.scrapers.FigshareApiScraper;
import org.perpetualnetworks.mdcrawler.scrapers.FigshareScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FigshareApiTask {
    @Autowired
    FigshareApiScraper figshareApiScraper;

    @Scheduled(fixedRate = 4*24*60*60*1000)
    public void run() {
        log.info("starting scheduled task figshare api scrape");
        figshareApiScraper.runScraper();
        log.info("endinging scheduled task figshare api scrape");
    }

}
