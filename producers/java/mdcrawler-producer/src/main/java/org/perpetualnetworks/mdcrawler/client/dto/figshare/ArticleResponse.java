package org.perpetualnetworks.mdcrawler.client.dto.figshare;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = ArticleResponse.ArticleResponseBuilder.class)
public class ArticleResponse {
    @JsonProperty("defined_type")
    String definedType;
    @JsonProperty("defined_type_name")
    String definedTypeName;
    @JsonProperty
    String doi;
    @JsonProperty("group_id")
    Integer groupId;
    @JsonProperty
    String handle;
    @JsonProperty
    Long id;
    @JsonProperty("published_date")
    String publishedDate;
    @JsonProperty("resource_doi")
    String resourceDoi;
    @JsonProperty("resource_title")
    String resourceTitle;
    @JsonProperty("thumb")
    String thumbNail;
    @JsonProperty("timeline")
    Object timeline;
    @JsonProperty("title")
    String title;
    @JsonProperty("url")
    String url;
    @JsonProperty("url_private_api")
    String urlPrivateApi;
    @JsonProperty("url_private_html")
    String urlPrivateHtml;
    @JsonProperty("url_public_api")
    String urlPublicApi;
    @JsonProperty("url_public_html")
    String urlPublicHtml;
}
