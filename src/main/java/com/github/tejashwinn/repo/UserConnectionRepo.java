package com.github.tejashwinn.repo;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.set.ReactiveSetCommands;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@ApplicationScoped
public class UserConnectionRepo {

    private ReactiveSetCommands<String, String> serverCommands;
    private ReactiveKeyCommands<String> keyCommands;

    public UserConnectionRepo(ReactiveRedisDataSource reactive) {
        serverCommands = reactive.set(String.class);
        keyCommands = reactive.key();
    }

    public void put(String userId, String server) {
        serverCommands.sadd(userId, server+System.nanoTime())
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
        serverCommands.srem(userId, server)
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
