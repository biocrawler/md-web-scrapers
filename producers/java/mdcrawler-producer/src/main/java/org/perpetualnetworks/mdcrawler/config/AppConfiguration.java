package org.perpetualnetworks.mdcrawler.config;

import org.perpetualnetworks.mdcrawler.services.metrics.MetricsService;
import org.perpetualnetworks.mdcrawler.services.metrics.MetricsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfiguration {

    @Autowired
    GraphiteConfiguration graphiteConfiguration;

    @Bean
    public MetricsService metricsService() {
        return new MetricsServiceImpl(graphiteConfiguration);
    }

}
