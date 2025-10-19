package com.github.tejashwinn.context.impl;

import com.github.tejashwinn.context.ServerContext;

import java.security.MessageDigest;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ConsistentHashing implements ServerContext {
    private final int virtualNodes;
    private final SortedMap<Long, String> ring = new TreeMap<>();

    public ConsistentHashing(List<String> nodes, int virtualNodes) {
        this.virtualNodes = virtualNodes;
        for (String node : nodes) addNode(node);
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(key.getBytes(UTF_8));
            long h = 0;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                h |= (digest[i] & 0xFF);
            }
            return h & 0xffffffffL;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.put(hash(node + "#" + i), node);
        }
    }

    @Override
    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.remove(hash(node + "#" + i));
        }
    }

    @Override
    public String getAssignedNode(String key) {
        if (ring.isEmpty()) return null;
        long h = hash(key);
        SortedMap<Long, String> tailMap = ring.tailMap(h);
        long nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        return ring.get(nodeHash);
    }
}
