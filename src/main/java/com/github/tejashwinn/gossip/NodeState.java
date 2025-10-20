package com.github.tejashwinn.gossip;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NodeState {
    // mapped by node id
    public Map<String, NodeInfo> members = new ConcurrentHashMap<>();


    public NodeState() {}
}