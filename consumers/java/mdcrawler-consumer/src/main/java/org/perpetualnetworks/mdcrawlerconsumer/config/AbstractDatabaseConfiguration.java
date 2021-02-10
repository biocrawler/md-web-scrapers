package org.perpetualnetworks.mdcrawlerconsumer.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Properties;

@Slf4j
abstract class AbstractDatabaseConfiguration {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @SneakyThrows
    Properties parseProperties(String credentialsFile) {
        JsonNode fileJson = MAPPER.readValue(
                new File(credentialsFile), JsonNode.class);
        Properties properties = new Properties();
        properties.put("user", fileJson.get("user").textValue());
        properties.put("password", fileJson.get("password").textValue());
        return properties;
    }
}
