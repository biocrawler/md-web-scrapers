package org.perpetualnetworks.mdcrawler.integration;

import io.netty.channel.ChannelOption;
import lombok.SneakyThrows;
import reactor.core.publisher.Flux;
import reactor.netty.Connection;
import reactor.netty.udp.UdpServer;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UdpTestServer {
    public static final String HOST = "localhost";
    public static final int TIMEOUT = 10;
    public static final String LATCH_SUCCESS_MESSAGE = "line was received";
    private final int port;
    private final CountDownLatch latch;

    public UdpTestServer(int port) {
        this.port = port;
        this.latch = new CountDownLatch(1);
    }

    private UdpServer buildUdpServer(Consumer<String> lineConsumer) {
        return reactor.netty.udp.UdpServer.create()
                .option(ChannelOption.SO_REUSEADDR, true)
                .host(HOST)
                .port(port)
                .handle((in, out) -> {
                    in.receive()
                            .asString()
                            .subscribe(lineConsumer);
                    return Flux.never();
                });
    }

    @SneakyThrows
    public void runServerWithAssertion(Consumer<String> lineConsumer, Consumer<Connection> doOnSuccess) {
        final Consumer<String> stringConsumer = lineConsumer.andThen(line -> {
            System.out.println("starting latch countdown");
            latch.countDown();
        });
        final Connection server = buildUdpServer(stringConsumer)
                .bind()
                .doOnSuccess(doOnSuccess)
                .block(Duration.ofSeconds(10));
        final boolean await = latch.await(TIMEOUT, TimeUnit.SECONDS);
        assertTrue(await, LATCH_SUCCESS_MESSAGE);
        if (server != null) {
            server.dispose();
        }
    }
}