package com.github.tejashwinn.message;

import com.github.tejashwinn.context.ServerContext;
import com.github.tejashwinn.gossip.NodeState;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MessageService {

    @Inject
    ServerContext serverContext;

    @Inject
    NodeState nodeState;


    @ConfigProperty(name = "quarkus.http.port")
    int port;

    HttpClient http = HttpClient.newHttpClient();


    @GET
    @Path("/send")
    public void sendMessage(
            @QueryParam("userId") String userId,
            @QueryParam("message") String message
    ) {
        String peer = serverContext.getAssignedNode(userId);
        if (peer.split(":")[1].equals(port)) {
            // send to using local service
        }
        try {
            URI uri = new URI("http://" + peer + "/gossip?");
            HttpRequest req = HttpRequest
                    .newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .build();
            http
                    .sendAsync(req, HttpResponse.BodyHandlers.ofString()).whenComplete((resp, ex) -> {
                        if (ex != null) {
                            log.info("Sent {} to {}", message, userId);
                        }
                    });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
