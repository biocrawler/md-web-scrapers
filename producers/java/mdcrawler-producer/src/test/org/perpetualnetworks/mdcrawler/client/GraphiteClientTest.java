package org.perpetualnetworks.mdcrawler.client;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.config.GraphiteConfiguration;

import java.util.Map;

class GraphiteClientTest {

    GraphiteConfiguration graphiteConfiguration = GraphiteConfiguration.builder()
            .host("mdcrawler-api.perpetualnetworks.org")
            .port(2003)
            .build();

    @Disabled
    @Test
    void sendMetric() {
        SimpleGraphiteClient client = new SimpleGraphiteClient(graphiteConfiguration);
        client.sendMetric("test.bob", 2);
    }

    @Disabled
    @Test
    void sendMetrics    () {
        SimpleGraphiteClient client = new SimpleGraphiteClient(graphiteConfiguration);
        client.sendMetrics(Map.of("test.bob", 2, "test.alice", 2.1, "test.joe", 2.2));
    }


}