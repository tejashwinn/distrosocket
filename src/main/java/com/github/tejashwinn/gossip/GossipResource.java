package com.github.tejashwinn.gossip;


import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
@Path("/gossip")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GossipResource {

    @Inject
    GossipService gossipService;

    @POST
    public Response receive(Map<String, NodeInfo> remote) {
        log.info(String.valueOf(remote));
        gossipService.mergeRemote(remote);
        return Response.ok(gossipService.getLocalMembership()).build();
    }


    @GET
    public Response membership() {
        log.info("Called for membership");
        return Response.ok(gossipService.getLocalMembership()).build();
    }
}