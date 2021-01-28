package org.perpetualnetworks.mdcrawlerconsumer.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Set;

@Data
@Builder
@JsonDeserialize(builder = FileArticle.FileArticleBuilder.class)
public class FileArticle {
    @JsonProperty("file_name")
    private String fileName;
    @JsonProperty
    private String url;
    @JsonProperty("download_url")
    private String downloadUrl;
    @JsonProperty("digital_object_id")
    private String digitalObjectId;
    @JsonProperty("file_description")
    private String fileDescription;
    @JsonProperty("refering_url")
    private String referingUrl;
    @JsonProperty
    private Set<String> keywords;
}
