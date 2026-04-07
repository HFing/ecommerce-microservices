package com.hfing.chatservice.controller;


import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SocketHandler {
    private final SocketIOServer socketIOServer;

    @OnConnect
    public void clientConnected(SocketIOClient client) {
        log.info("Client connected: {}", client.getSessionId());
    }

    @OnDisconnect
    public void clientDisconnected(SocketIOClient client) {
        log.info("Client disConnected: {}", client.getSessionId());
    }

    @PostConstruct
    public void startServer() {
        socketIOServer.start();
        socketIOServer.addListeners(this);
        log.info("Socket server started");
    }

    @PreDestroy
    public void stopServer() {
        socketIOServer.stop();
        log.info("Socket server stoped");
    }
}
