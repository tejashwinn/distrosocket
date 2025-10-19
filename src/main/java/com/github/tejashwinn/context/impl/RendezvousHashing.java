package com.github.tejashwinn.context.impl;

import com.github.tejashwinn.context.ServerContext;
import lombok.SneakyThrows;

import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

public class RendezvousHashing implements ServerContext {

    private final Set<String> servers;
    private final MessageDigest md;

    @SneakyThrows
    public RendezvousHashing(Set<String> servers) {
        this.servers = new HashSet<>(servers);
        this.md = MessageDigest.getInstance("SHA-256");
    }

    private long getScore(String server, String key) {
        md.reset();
        md.update((server + ":" + key).getBytes());
        byte[] digest = md.digest();
        // Convert the first 8 bytes of the digest to a long
        return ((long) (digest[0] & 0xFF) << 56) |
                ((long) (digest[1] & 0xFF) << 48) |
                ((long) (digest[2] & 0xFF) << 40) |
                ((long) (digest[3] & 0xFF) << 32) |
                ((long) (digest[4] & 0xFF) << 24) |
                ((long) (digest[5] & 0xFF) << 16) |
                ((long) (digest[6] & 0xFF) << 8) |
                ((long) (digest[7] & 0xFF));
    }

    @Override
    public String getServer(String key) {
        if (servers.isEmpty()) {
            return null;
        }
        String bestServer = null;
        long highestScore = Long.MIN_VALUE;

        for (String server : servers) {
            long currentScore = getScore(server, key);
            if (currentScore > highestScore) {
                highestScore = currentScore;
                bestServer = server;
            }
        }
        return bestServer;
    }

    @Override
    public void addServer(String server) {
        this.servers.add(server);
    }

    @Override
    public void removeServer(String server) {
        this.servers.remove(server);
    }
}
