package com.github.tejashwinn.gossip;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeInfo {
    private String id;
    private long heartbeat;
    private Instant lastSeen;

    public NodeInfo(String id, long heartbeat) {
        this.id = id;
        this.heartbeat = heartbeat;
    }
}