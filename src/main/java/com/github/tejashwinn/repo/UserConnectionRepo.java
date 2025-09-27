package com.github.tejashwinn.repo;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.set.ReactiveSetCommands;
import io.quarkus.redis.datasource.set.SetCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class UserConnectionRepo {

    public record UserConnection(List<String> servers) {
    }

    private ReactiveKeyCommands<String> keyCommands;
    private ReactiveSetCommands<String, String> serverCommands;

    public UserConnectionRepo(ReactiveRedisDataSource reactive) {
        serverCommands = reactive.set(String.class);
        keyCommands = reactive.key();
    }

    public void put(String userId, String server) {
        serverCommands.sadd(userId, server);
    }

    public void remove(String userId, String server) {
        serverCommands.srem(userId, server);
    }
}
