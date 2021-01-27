package org.perpetualnetworks.mdcrawler.converters;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawler.defaults.ArticleDefaults;
import org.perpetualnetworks.mdcrawler.models.Article;
import org.perpetualnetworks.mdcrawler.scrapers.dto.MendeleyResponse;
import org.perpetualnetworks.mdcrawler.utils.ByteOperations;
import org.perpetualnetworks.mdcrawler.utils.lzw.LZWCompressor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MendeleyArticleConverterTest {

    @Test
    @SneakyThrows
    void build_article_ok() {
        Article a = ArticleDefaults.anArticle().build();
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(a));
    }

    @Disabled("words with raw data")
    @Test
    @SneakyThrows
    void convert_raw_article() {
        ObjectMapper mapper = new ObjectMapper();
        final String testDataPath = "src/test/org/perpetualnetworks/mdcrawler/utils/testData_2_raw.txt";
        File testData = new File(testDataPath);
        String bigString = FileUtils.readFileToString(testData, "UTF-8");
        MendeleyArticleConverter converter = new MendeleyArticleConverter();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final MendeleyResponse mendeleyResponse = mapper.readValue(bigString, MendeleyResponse.class);
        final Optional<Article> convert = mendeleyResponse.getResults().stream().map(converter::convert).flatMap(Optional::stream).findFirst();
        assert convert.isPresent();
        byte[] articleBytes = mapper.writeValueAsBytes(convert.get());
        int artByteSize = 0;
        for (byte b : articleBytes) {
            artByteSize++;
        }
        System.out.println("article bytes size: " + artByteSize);
        int artByteCompressedSize = 0;
        LZWCompressor compressor = new LZWCompressor();
        final byte[] articleBytesCompressed = compressor.compress(articleBytes);
        for (byte c : articleBytesCompressed) {
            artByteCompressedSize++;
        }
        System.out.println("article compressed bytes size: " + artByteCompressedSize);
        System.out.println("compression gained: " + (1 - (float) artByteCompressedSize / artByteSize) * 100);
        final String content = new String(compressor.decompress(articleBytesCompressed), StandardCharsets.UTF_8);
        final Article article = mapper.readValue(content, Article.class);
        final Article rebuiltArticle = article.toBuilder().keywords(
                article.getKeywords()
                        .stream()
                        .map(ByteOperations::convertStringBytesToString)
                        .collect(Collectors.toSet())).build();
        System.out.println(rebuiltArticle);
    }

    @Test
    void bytesToKeyword() {
        String result = "Molecular Dynamics Study";
        String testKw = "[77, 111, 108, 101, 99, 117, 108, 97, 114, 32, 68, 121, 110, 97, 109, 105, 99, 115, 32, 83, 116, 117, 100, 121]";
        final String finalString = ByteOperations.convertStringBytesToString(testKw);
        assertEquals(result, finalString);
    }

}