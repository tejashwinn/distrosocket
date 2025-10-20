package com.github.tejashwinn.gossip;

import com.github.tejashwinn.context.ServerContext;
import com.github.tejashwinn.context.impl.ConsistentHashing;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
@ApplicationScoped
public class GossipProducer {


    @Produces
    @ApplicationScoped
    public NodeState createMyDependency() {
        return new NodeState();
    }
}
