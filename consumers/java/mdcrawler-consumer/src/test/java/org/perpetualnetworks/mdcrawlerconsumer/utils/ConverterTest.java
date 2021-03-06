package org.perpetualnetworks.mdcrawlerconsumer.utils;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.perpetualnetworks.mdcrawlerconsumer.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConverterTest {


    @Test
    @SneakyThrows
    void dateParse() {
        String testDate = "Tue Mar 02 12:52:08 GMT 2021";
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.Time.dateStringsPattern);
        Date d = simpleDateFormat.parse(testDate);
    }
}
