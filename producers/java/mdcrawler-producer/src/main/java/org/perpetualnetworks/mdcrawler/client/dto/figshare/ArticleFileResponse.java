package org.perpetualnetworks.mdcrawler.client.dto.figshare;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = ArticleFileResponse.ArticleFileResponseBuilder.class)
public class ArticleFileResponse {
    @JsonProperty("is_link_only")
    Boolean isLinkOnly;
    @JsonProperty("name")
    String fileName;
    @JsonProperty("supplied_md5")
    String suppliedMd5;
    @JsonProperty("computed_md5")
    String computedMd5;
    @JsonProperty
    Integer id;
    @JsonProperty("download_url")
    String downloadUrl;
    @JsonProperty
    Integer size;
}
