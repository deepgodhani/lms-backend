package com.versionxd.lms.backend.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SocketIOService {

    @Autowired
    private SocketIOServer server;

    // A simple way to store which users are in which room
    private final Map<String, String> sessionRoomMap = new HashMap<>();
    private final Map<String, String> sessionUserMap = new HashMap<>();

    @PostConstruct
    private void init() {
        server.addConnectListener(client -> {
            System.out.println("Socket.IO Client connected: " + client.getSessionId());
        });

        server.addDisconnectListener(client -> {
            System.out.println("Socket.IO Client disconnected: " + client.getSessionId());
            String classId = sessionRoomMap.get(client.getSessionId().toString());
            String userId = sessionUserMap.get(client.getSessionId().toString());
            if (classId != null && userId != null) {
                // Notify everyone else in the room that this user has left
                server.getRoomOperations(classId).sendEvent("user-left", userId);
                sessionRoomMap.remove(client.getSessionId().toString());
                sessionUserMap.remove(client.getSessionId().toString());
            }
        });

        // Event for when a user joins a class
        server.addEventListener("join-class", Map.class, (client, data, ackSender) -> {
            String classId = data.get("classId").toString();
            String userId = data.get("userId").toString();

            // Join the Socket.IO room
            client.joinRoom(classId);
            sessionRoomMap.put(client.getSessionId().toString(), classId);
            sessionUserMap.put(client.getSessionId().toString(), userId);

            // Get a list of all other users already in the room
            List<String> otherUsers = server.getRoomOperations(classId).getClients().stream()
                                            .map(c -> sessionUserMap.get(c.getSessionId().toString()))
                                            .filter(id -> id != null && !id.equals(userId))
                                            .collect(Collectors.toList());

            // Send this list to the new user
            client.sendEvent("all-users", otherUsers);
            System.out.println("User " + userId + " joined class: " + classId);
        });

        // Event for the initial WebRTC signal (Offer)
        server.addEventListener("sending-signal", Map.class, (client, data, ackSender) -> {
            String userToSignal = data.get("userToSignal").toString();
            String callerID = data.get("callerID").toString();
            Object signal = data.get("signal");

            // Find the client we need to send the signal to
            SocketIOClient targetClient = findClientByUserId(userToSignal);
            if (targetClient != null) {
                targetClient.sendEvent("user-joined", Map.of(
                        "signal", signal,
                        "callerID", callerID
                ));
            }
        });

        // Event for the returning WebRTC signal (Answer)
        server.addEventListener("returning-signal", Map.class, (client, data, ackSender) -> {
            String callerID = data.get("callerID").toString();
            Object signal = data.get("signal");

            // Find the original caller to send the answer back
            SocketIOClient targetClient = findClientBySocketId(callerID);
            if (targetClient != null) {
                targetClient.sendEvent("receiving-returned-signal", Map.of(
                        "signal", signal,
                        "id", client.getSessionId().toString()
                ));
            }
        });
    }

    private SocketIOClient findClientByUserId(String userId) {
        return sessionUserMap.entrySet().stream()
                             .filter(entry -> entry.getValue().equals(userId))
                             .map(entry -> server.getClient(java.util.UUID.fromString(entry.getKey())))
                             .findFirst().orElse(null);
    }

    private SocketIOClient findClientBySocketId(String socketId) {
        // This is a simplified lookup. In a real app, you might need a reverse map.
        // For simple-peer, the callerID is the *socket ID*, not the user ID.
        return server.getAllClients().stream()
                     .filter(c -> c.getSessionId().toString().equals(socketId))
                     .findFirst().orElse(null);
    }

    @PreDestroy
    private void tearDown() {
        server.stop();
    }

    public void startServer() {
        server.start();
    }
}