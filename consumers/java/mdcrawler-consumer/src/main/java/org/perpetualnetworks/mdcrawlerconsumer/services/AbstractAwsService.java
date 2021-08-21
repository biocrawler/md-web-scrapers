package org.perpetualnetworks.mdcrawlerconsumer.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.perpetualnetworks.mdcrawlerconsumer.config.AwsConfiguration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;

import java.io.File;
import java.util.Optional;

@Slf4j
public abstract class AbstractAwsService {

    private static final String AWS_SECRET_ACCESS_KEY = "aws_secret_access_key";
    private static final String AWS_ACCESS_KEY_ID = "aws_access_key_id";

    private final AwsConfiguration awsConfiguration;
    private AwsBasicCredentials awsBasicCredentials;


    public AbstractAwsService(AwsConfiguration awsConfiguration) {
        this.awsConfiguration = awsConfiguration;

    }

    public AwsBasicCredentials getAwsBasicCredentials() {
        parseAwsCredentials(awsConfiguration).ifPresent(c -> this.awsBasicCredentials = c);
        return awsBasicCredentials;
    }

    @SneakyThrows
    private Optional<AwsBasicCredentials> parseAwsCredentials(AwsConfiguration awsConfiguration) {
        try {
            File src = new File(awsConfiguration.getCredentialsFile());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode fileJson = mapper.readValue(src, JsonNode.class);
            return Optional.of(buildAwsBasicCredentials(fileJson));
        } catch (Exception e) {
            log.error("could not parse aws credentials from config: " + awsConfiguration);
        }
        return Optional.empty();
    }

    private AwsBasicCredentials buildAwsBasicCredentials(JsonNode fileJson) {
        final String accessKeyId = fileJson.get(AWS_ACCESS_KEY_ID).asText();
        final String secretAccessKey = fileJson.get(AWS_SECRET_ACCESS_KEY).asText();
        return AwsBasicCredentials.create(accessKeyId,
                secretAccessKey);
    }

}
