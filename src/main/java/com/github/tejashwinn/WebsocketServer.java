package com.github.tejashwinn;

import com.github.tejashwinn.repo.UserConnectionRepo;
import com.github.tejashwinn.util.NodeUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Shutdown;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
@ServerEndpoint("/connections/{username}")
public class WebsocketServer {

    private final UserConnectionRepo userConnectionRepo;

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        broadcast("User " + username + " joined");
        sessions.put(username, session);
        userConnectionRepo.put(username, NodeUtil.NODE_ID);
    }

    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessions.remove(username);
        broadcast("User " + username + " left");
        userConnectionRepo.remove(username, NodeUtil.NODE_ID);
    }

    @OnError
    public void onError(Session session, @PathParam("username") String username, Throwable throwable) {
        sessions.remove(username);
        userConnectionRepo.remove(username, NodeUtil.NODE_ID);
        broadcast("User " + username + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("username") String username) {
        broadcast(">> " + username + ": " + message);
    }

    private void broadcast(String message) {
        log.info(message);
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    System.out.println("Unable to send message: " + result.getException());
                }
            });
        });
    }


    public void onStop(@Observes Shutdown ev) {
        log.info("The application is stopping...");
        sessions.keySet().forEach(
                k -> userConnectionRepo.remove(k, NodeUtil.NODE_ID)
        );
    }

}
