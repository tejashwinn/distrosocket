package com.github.tejashwinn.gossip;

import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@ApplicationScoped
public class GossipService {


    final NodeState state = new NodeState();
    volatile long localHeartbeat = 0;


    @ConfigProperty(name = "quarkus.http.port")
    int port;


    @ConfigProperty(name = "gossip.seed-peers")
    String seedPeers;


    @ConfigProperty(name = "gossip.fanout", defaultValue = "3")
    int fanout;

    HttpClient http = HttpClient.newHttpClient();


    String myId;


    @PostConstruct
    void init() {
        myId = getMyId();
        NodeInfo me = new NodeInfo(myId, localHeartbeat);
        state.members.put(myId, me);
        if (seedPeers != null && !seedPeers.isBlank()) {
            String[] arr = seedPeers.split(",");
            for (String s : arr) {
                s = s.trim();
                if (!s.isEmpty() && !s.equals(myId)) {
                    state.members.putIfAbsent(s, new NodeInfo(s, 0));
                }
            }
        }
    }


    String getMyId() {
        String host = "localhost";
        return host + ":" + port;
    }


    @Scheduled(every = "${gossip.heartbeat-interval:2000}ms")
    synchronized void tickHeartbeat() {
        localHeartbeat++;
        NodeInfo me = new NodeInfo(myId, localHeartbeat);
        state.members.put(myId, me);
    }

    @Scheduled(every = "${gossip.push-interval:3000}ms")
    void pushGossip() {
        List<String> peers = new ArrayList<>(state.members.keySet());
        peers.remove(myId);
        if (peers.isEmpty()) return;
        Collections.shuffle(peers);
        int k = Math.min(fanout, peers.size());
        for (int i = 0; i < k; i++) {
            String peer = peers.get(i);
            sendStateToPeer(peer);
        }
    }


    void sendStateToPeer(String peer) {
        try {
            URI uri = new URI("http://" + peer + "/gossip");
            String body = JsonUtil.toJson(state.members);
            HttpRequest req = HttpRequest.newBuilder().uri(uri).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
            http.sendAsync(req, HttpResponse.BodyHandlers.ofString()).whenComplete((resp, ex) -> {
                if (ex != null) {
                    NodeInfo n = state.members.get(peer);
                    if (n != null) n.setLastSeen(Instant.now());
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void mergeRemote(Map<String, NodeInfo> remote) {
        for (Map.Entry<String, NodeInfo> e : remote.entrySet()) {
            String id = e.getKey();
            NodeInfo remoteInfo = e.getValue();
            NodeInfo local = state.members.get(id);
            if (local == null) {
                state.members.put(id, new NodeInfo(id, remoteInfo.getHeartbeat()));
            } else {
                if (remoteInfo.getHeartbeat() > local.getHeartbeat()) {
                    local.setHeartbeat(remoteInfo.getHeartbeat());
                    local.setLastSeen(Instant.now());
                }
            }
        }
    }


    public Map<String, NodeInfo> getLocalMembership() {
        return state.members;
    }
}