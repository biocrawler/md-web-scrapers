package org.perpetualnetworks.mdcrawler.converters;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.defaults.ArticleDefaults;
import org.perpetualnetworks.mdcrawler.models.Article;

class MendeleyArticleConverterTest {

    @Test
    @SneakyThrows
    void build_article_ok() {
        Article a = ArticleDefaults.anArticle().build();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(a));
    }

}