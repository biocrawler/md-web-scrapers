package org.perpetualnetworks.mdcrawler.utils;

import lombok.SneakyThrows;

import java.net.InetAddress;

public class LocalCommands {

    @SneakyThrows
    public static String getHostName() {
        return InetAddress.getLocalHost().getHostName();

    }
}
