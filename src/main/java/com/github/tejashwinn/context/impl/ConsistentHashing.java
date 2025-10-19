package com.github.tejashwinn.context.impl;

import com.github.tejashwinn.context.ServerContext;
import lombok.SneakyThrows;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ConsistentHashing implements ServerContext {
    private final TreeMap<Long, String> ring;
    private final int numberOfReplicas;
    private final MessageDigest md;

    @SneakyThrows
    public ConsistentHashing(int numberOfReplicas) {
        this.ring = new TreeMap<>();
        this.numberOfReplicas = numberOfReplicas;
        this.md = MessageDigest.getInstance("MD5");
    }

    public void addServer(String server) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = generateHash(server + i);
            ring.put(hash, server);
        }
    }

    public void removeServer(String server) {
        for (int i = 0; i < numberOfReplicas; i++) {
            long hash = generateHash(server + i);
            ring.remove(hash);
        }
    }

    public String getServer(String key) {
        if (ring.isEmpty()) {
            return null;
        }
        long hash = generateHash(key);
        if (!ring.containsKey(hash)) {
            hash = ring.ceilingKey(hash);
        }
        return ring.get(hash);
    }

    private long generateHash(String key) {
        md.reset();
        md.update(key.getBytes());
        byte[] digest = md.digest();
        return ((long) (digest[3] & 0xFF) << 24) |
                ((long) (digest[2] & 0xFF) << 16) |
                ((long) (digest[1] & 0xFF) << 8) |
                ((long) (digest[0] & 0xFF));
    }
}
