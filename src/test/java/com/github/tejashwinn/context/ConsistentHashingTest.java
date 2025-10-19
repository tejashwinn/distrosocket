package com.github.tejashwinn.context;

import com.github.tejashwinn.context.impl.ConsistentHashing;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNull;

class ConsistentHashingTest {

    private static ExecutorService executors;

    @BeforeAll
    public static void init() {
        executors = Executors.newFixedThreadPool(10);
    }

    @AfterAll
    public static void tearDown() {
        executors.close();
    }


    @Test
    void test() {
        ConsistentHashing consistentHashing = new ConsistentHashing(10);
        List<Future<?>> futures = new ArrayList<>();
        try {
            for (int i = 0; i < 10; i++) {
                String serverName = String.valueOf(i);
                Future<?> future = executors.submit(() -> consistentHashing.addServer(serverName));
                futures.add(future);
            }
            executors.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}