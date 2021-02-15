package org.perpetualnetworks.mdcrawlerconsumer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@JsonDeserialize(builder = Article.ArticleBuilder.class)
public class Article {
    @JsonProperty
    private String title;
    @JsonProperty("source_url")
    private String sourceUrl;
    // relation
    @JsonProperty
    private Set<String> keywords;
    @JsonProperty("digital_object_id")
    private String digitalObjectId;
    @JsonProperty
    private String description;
    @JsonProperty("parse_date")
    private String parseDate;
    @JsonProperty("upload_date")
    private String uploadDate;
    @JsonProperty("created_date")
    private String createdDate;
    @JsonProperty("modified_date")
    private String modifiedDate;
    // relation
    @JsonProperty
    private Set<ArticleFile> files;
    // relation
    @JsonProperty
    private Set<Author> authors;
    @JsonProperty("refering_url")
    private String referingUrl;  //was parent request url
    @JsonProperty
    private Boolean enriched;
    @JsonProperty
    private Boolean parsed;
    @JsonProperty
    private Boolean published;
    @JsonProperty("additional_data")
    private AdditionalData additionalData;

    @Data
    @Builder(toBuilder = true)
    @JsonDeserialize(builder = AdditionalData.AdditionalDataBuilder.class)
    public static class AdditionalData {
        @JsonProperty("figshare_type")
        private String figshareType;
        @JsonProperty("lab_details")
        private List<String> labDetails;
    }

}
