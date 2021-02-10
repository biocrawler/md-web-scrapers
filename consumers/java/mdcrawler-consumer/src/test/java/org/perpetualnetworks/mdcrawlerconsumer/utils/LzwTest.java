package org.perpetualnetworks.mdcrawlerconsumer.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.models.Article;
import org.perpetualnetworks.mdcrawlerconsumer.utils.lzw.LZwCompressor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LzwTest {

    //TODO: add this method for conversion
    private byte[] convert(List<Byte> list) {
        byte[] result = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    @Test
    void message_test_2() {
        LZwCompressor compressor = new LZwCompressor();
        byte[] collect = convert(Arrays.stream("[-4,0,123,0,34,0,116,0,105,0,116,0,108,0,101,0,34,0,58,1,1,0,101,0,115,0,116,0,32,1,2,1,4,1,6,0,44,0,34,0,107,0,101,0,121,0,119,0,111,0,114,0,100,0,115,1,7,0,91,0,34,0,87,0,79,0,82,0,68,0,34,1,17,1,22,1,24,0,51,1,34,0,34,1,36,0,100,0,32,0,49,1,39,1,41,0,32,0,50,0,34,0,93,1,17,0,100,1,10,0,99,0,114,0,105,0,112,1,2,0,111,0,110,1,7,1,9,1,11,0,32,1,52,0,115,1,54,1,56,1,58,1,60,1,17,0,102,0,105,1,5,1,26,0,58,0,91,1,0,0,117,0,114,0,108,1,7,0,110,0,117,0,108,0,108,1,17,1,19,1,21,1,23,1,25,1,27,0,34,0,99,0,97,0,116,1,49,1,71,1,73,0,101,0,95,0,110,0,97,0,109,1,6,1,8,0,116,1,10,1,12,1,72,1,5,1,39,0,100,0,111,0,119,0,110,0,108,0,111,0,97,0,100,0,95,1,79,1,81,1,8,0,104,0,116,0,116,0,112,0,115,0,58,0,47,0,47,1,110,0,101,1,113,1,115,1,117,1,119,0,47,1,107,1,11,1,112,0,105,0,103,1,3,0,97,0,108,0,95,0,111,0,98,0,106,0,101,0,99,0,116,0,95,0,105,0,100,1,61,0,49,0,48,0,46,0,49,0,50,0,46,0,48,1,-91,0,47,0,50,0,48,0,49,0,54,0,45,0,48,0,50,0,55,0,120,0,45,0,49,1,-80,0,50,0,120,0,57,1,39,1,-123,0,95,1,65,1,67,1,57,0,105,1,59,1,61,0,98,1,-107,1,39,0,114,0,101,0,102,0,101,1,55,0,110,0,103,1,121,1,80,1,61,1,125,1,127,1,-127,1,-125,1,-110,0,105,0,99,0,101,0,46,0,119,1,81,0,125,1,50,0,34,0,97,0,117,0,116,0,104,1,23,1,75,1,83,1,85,1,17,0,101,0,110,1,55,0,99,0,104,1,-122,1,7,0,102,1,-110,0,115,0,101,1,17,0,112,0,117,0,98,0,108,0,105,0,115,1,-24,1,-99,0,58,0,116,0,114,0,117,1,-18,0,34,0,115,0,111,1,79,1,-45,1,-54,1,123,0,34,1,-51,1,-128,1,-126,1,-117,1,108,0,46,0,99,0,111,0,109,2,8,1,-115,1,51,1,-113,1,-111,1,-109,1,-107,1,-105,1,-103,1,-101,1,-9,0,34,1,-97,1,-95,1,-88,0,53,0,51,1,-89,1,-87,1,-85,1,-83,0,54,1,-80,1,-78,0,45,1,-76,1,-74,1,-17,0,97,0,114,1,-19,1,-71,1,95,1,105,2,25,0,57,0,55,0,48,1,-84,0,49,2,52,0,84,1,-91,0,58,2,56,1,-91,1,39,0,117,0,112,1,-119,1,120,0,100,2,46,1,-98,2,49,2,51,1,-87,2,54,2,58,0,48,2,57,0,48,1,-62,1,-60,1,-58,0,105,1,-56,2,2,1,82,1,84,1,86,1,-38,0,100,0,100,1,3,1,-67,1,102,1,-109,2,65,0,116,0,97,2,82,1,85,0,125]".replace("[", "").replace("]", "").split(",")).map(Integer::valueOf).map(Integer::byteValue).collect(Collectors.toList()));
        String bob = new String(compressor.decompress(collect), StandardCharsets.UTF_8);
        System.out.println(bob);
    }

    @Test
    void compressor_test() {
        String testString = "TOBEORNOTTOBEORTOBEORNOT";
        LZwCompressor lzwCompressor = new LZwCompressor();
        byte[] compressed = lzwCompressor.compress(testString.getBytes(StandardCharsets.UTF_8));
        byte[] decompressed = lzwCompressor.decompress(compressed);
        assertEquals(testString, new String(decompressed, StandardCharsets.UTF_8));
    }

    //TODO: fix keyword conversion from bytes
    @Disabled("requires a compressed article stored as testData.txt")
    @Test
    @SneakyThrows
    void message_compression_test_3() {
        LZwCompressor lzwCompressor = new LZwCompressor();
        final String testDataPath = "src/test/org.perpetualnetworks.mdcrawlerconsumer/utils/testData_1.txt";
        File testData = new File(testDataPath);
        String listOfCompressedStrings = FileUtils.readFileToString(testData, "UTF-8");
        final String processedArticleString = Arrays.stream(
                listOfCompressedStrings.split(","))
                .filter(Objects::nonNull)
                .map(ByteOperations::convertCompressedStringToBytes)
                .map(lzwCompressor::decompress)
                .map(bytes -> new String(bytes, StandardCharsets.UTF_8))
                .collect(Collectors.joining(" "));
        ObjectMapper mapper = new ObjectMapper();
        final Article article = mapper.readValue(processedArticleString, Article.class);

        System.out.println(article.toBuilder()
                .keywords(article.getKeywords().stream()
                        .map(ByteOperations::convertStringBytesToString)
                        .collect(Collectors.toSet()))
                .build());

    }
}