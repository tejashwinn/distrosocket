package com.github.tejashwinn;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.WebsocketClientSpec;

import java.time.Duration;

@Slf4j
public class Client {
    @SneakyThrows
    public static void main(String[] args) {
        String uri = "ws://localhost:8080/connections/";
        int connections = 10000;
        Flux.range(1, connections)
                .flatMap(id ->
                        HttpClient.create()
                                .websocket(WebsocketClientSpec.builder().handlePing(true).build())
                                .uri(uri + (id % 10 + System.nanoTime()))
                                .handle((inbound, outbound) -> {
                                    // Keep receiving messages indefinitely
                                    inbound.receive()
                                            .asString()
                                            .doOnNext(msg -> System.out.println("Client " + id + " received: " + msg))
                                            .subscribe();

                                    // Optionally send periodic messages
                                    Flux.interval(Duration.ofSeconds(5))
                                            .flatMap(tick -> outbound.sendString(Flux.just("Ping from client " + id)))
                                            .subscribe();

                                    // Never complete
                                    return outbound.neverComplete();
                                })
                                .doOnSubscribe(sub -> System.out.println("Client " + id + " connected"))
                                .doOnError(throwable -> log.error(throwable.getMessage(), throwable))
                )
                .subscribe();

        // Keep main thread alive indefinitely
        while (true) {
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
