package org.perpetualnetworks.mdcrawler.utils;

import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteOperations {

    public static String convertStringBytesToString(String stringListBytes) {
        final String[] split = removeNewLinesAndBrackets(stringListBytes)
                .replace(" ", "")
                .split(",");
        final byte[] bytes = parseByteList(split);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] convertCompressedStringToBytes(String stringListBytes) {
        final String[] split = removeNewLinesAndBrackets(stringListBytes)
                .split(" ");
        return parseByteList(split);
    }

    @Nonnull
    private static byte[] parseByteList(String[] split) {
        Byte[] bytes = Arrays.stream(split)
                .map(Byte::parseByte)
                .toArray(Byte[]::new);
        return ArrayUtils.toPrimitive(bytes);
    }

    private static String removeNewLinesAndBrackets(String stringListBytes) {
        return stringListBytes
                .replace("\n", "")
                .replace("]", "")
                .replace("[", "");
    }

}
