package com.github.tejashwinn.repo;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.set.ReactiveSetCommands;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class UserConnectionRepo {

    private final ReactiveSetCommands<String, String> valueCommands;

    public UserConnectionRepo(ReactiveRedisDataSource reactive) {
        valueCommands = reactive.set(String.class);
    }

    public void put(String userId, String server) {
        valueCommands
                .sadd(userId, server)
                .subscribe()
                .with(
                        result -> {
                            log.info("Element added to redis: {}", server);
                        }, error -> {
                            log.info("Error adding element: {}", server, error);
                        }

                );
    }

    public void remove(String userId, String server) {
        valueCommands.srem(userId, server)
                .subscribe()
                .with(
                        result -> {
                            log.info("Removed from redis: {}", server);
                        }, error -> {
                            log.info("Error removing element: {}", server, error);
                        }

                );
    }





}
