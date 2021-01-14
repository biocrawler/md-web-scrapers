package org.perpetualnetworks.mdcrawler.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Set;

@Builder
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
