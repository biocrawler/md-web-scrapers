package org.perpetualnetworks.mdcrawler.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MetricsConfiguration {
    @JsonProperty
    String componentType;
    @JsonProperty
    String nameSpace;
}
