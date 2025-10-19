package com.github.tejashwinn.context;

public interface ServerContext {

    void addServer(String server);

    void removeServer(String server);

    String getServer(String key);
}
