package org.perpetualnetworks.mdcrawlerconsumer.presentation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder(toBuilder = true)
@JsonDeserialize(builder = Page.PageBuilder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
class Page<T> {
    @JsonProperty
    Integer size;
    @JsonProperty
    List<T> content;
    @JsonProperty
    Integer totalPages;
    @JsonProperty
    Integer totalElements;
    @JsonProperty
    boolean hasNext = false;
    @JsonProperty
    String nextUrl;
    @JsonProperty
    String previousUrl;

    public boolean hasContent() {
        return content.isEmpty();
    }
}
