package org.perpetualnetworks.mdcrawler.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonDeserialize(builder = Author.AuthorBuilder.class)
public class Author {
    @JsonProperty
    private String name;
}
