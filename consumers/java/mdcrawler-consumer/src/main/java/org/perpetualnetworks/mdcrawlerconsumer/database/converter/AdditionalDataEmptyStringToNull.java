package org.perpetualnetworks.mdcrawlerconsumer.database.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class AdditionalDataEmptyStringToNull implements AttributeConverter<String, String> {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(String string) {
        // Use defaultIfEmpty to preserve Strings consisting only of whitespaces
        return StringUtils.defaultIfBlank(string, null);
    }

    @Override
    @SneakyThrows
    public String convertToEntityAttribute(String dbData) {
        //If you want to keep it null otherwise transform to empty String
        final Article.AdditionalData additionalData = mapper.readValue(dbData, Article.AdditionalData.class);
        return additionalData.toString();
    }
}
