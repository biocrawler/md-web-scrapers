package org.perpetualnetworks.mdcrawlerconsumer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@JsonDeserialize(builder = ArticleFile.ArticleFileBuilder.class)
public class ArticleFile {
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty
    private String url;
    @JsonProperty
    private String size;
    @JsonProperty("download_url")
    private String downloadUrl;
    @JsonProperty("digital_object_id")
    private String digitalObjectId;
    @JsonProperty("file_description")
    private String fileDescription;
    @JsonProperty("refering_url")
    private String referingUrl;
    //relation
    @JsonProperty
    private Set<String> keywords;
}
