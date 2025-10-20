package com.github.tejashwinn.repo;

import com.github.tejashwinn.util.NodeUtil;
import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ReactiveValueCommands;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import lombok.extern.slf4j.Slf4j;

import static com.github.tejashwinn.util.NodeUtil.NODE_ID;

@Slf4j
@ApplicationScoped
public class NodeRegistryRepo {

    private final ReactiveValueCommands<String, String> valueCommands;
    private final ReactiveKeyCommands<String> keyCommands;

    public NodeRegistryRepo(ReactiveRedisDataSource reactive) {
        valueCommands = reactive.value(String.class);
        keyCommands = reactive.key();
    }

    public void put(String nodeId) {
        valueCommands
                .set(nodeId, "")
                .subscribe()
                .with(
                        result -> {
                            log.info("Element added to redis: {}", nodeId);
                        }, error -> {
                            log.info("Error adding element: {}", nodeId, error);
                        }
                );
    }

    public void remove(String nodeId) {
        keyCommands.del(nodeId)
                .subscribe()
                .with(
                        result -> {
                            log.info("Removed from redis: {}", nodeId);
                        }, error -> {
                            log.info("Error removing element: {}", nodeId, error);
                        }
                );
    }

    public void onStart(@Observes StartupEvent ev) {
        log.info("The application is starting... adding node id to redis: {}", NODE_ID);
        put("server:" + NODE_ID);
    }

    public void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping... adding node id to redis: {}", NODE_ID);
        remove("server:" + NODE_ID);
    }

}
