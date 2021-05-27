package org.perpetualnetworks.mdcrawler.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("figshareApi")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FigshareApiConfiguration {
    @JsonProperty("searchTerms")
    String DefaultSearchTerms;
    @JsonProperty("dateFormat")
    String DefaultDateFormat;
}
