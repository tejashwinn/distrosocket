package com.github.tejashwinn.context;

public interface ServerContext {

    void addNode(String node);
    void removeNode(String node);
    String getAssignedNode(String key);
}
