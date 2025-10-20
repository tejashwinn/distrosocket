package com.github.tejashwinn.context;

import com.github.tejashwinn.context.impl.ConsistentHashing;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

import java.util.Collections;

@ApplicationScoped
public class ServerContextProducer {

    @Produces
    @ApplicationScoped
    public ServerContext createMyDependency() {
        return new ConsistentHashing(Collections.emptyList(), 10);
    }
}
