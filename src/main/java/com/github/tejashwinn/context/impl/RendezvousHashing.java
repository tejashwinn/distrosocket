package com.github.tejashwinn.context.impl;

import com.github.tejashwinn.context.ServerContext;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RendezvousHashing implements ServerContext {

    private final Set<String> nodes = new HashSet<>();

    public RendezvousHashing(Collection<String> initialNodes) {
        nodes.addAll(initialNodes);
    }

    private long hash(String key, String node) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest((key + ":" + node).getBytes(UTF_8));
            long h = 0;
            for (int i = 0; i < 8; i++) {
                h <<= 8;
                h |= (digest[i] & 0xFF);
            }
            return h;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addNode(String node) {
        nodes.add(node);
    }

    @Override
    public void removeNode(String node) {
        nodes.remove(node);
    }

    @Override
    public String getAssignedNode(String key) {
        String bestNode = null;
        long bestScore = Long.MIN_VALUE;
        for (String node : nodes) {
            long score = hash(key, node);
            if (score > bestScore) {
                bestScore = score;
                bestNode = node;
            }
        }
        return bestNode;
    }
}
