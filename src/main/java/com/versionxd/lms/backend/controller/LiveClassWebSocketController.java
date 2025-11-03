package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.model.WebSocketMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class LiveClassWebSocketController {

    /**
     * This method handles messages sent to "/app/live-class/{classId}".
     * The @SendTo annotation broadcasts the return value to all subscribers of "/topic/live-class/{classId}".
     */
    @MessageMapping("/live-class/{classId}")
    @SendTo("/topic/live-class/{classId}")
    public WebSocketMessage handleClassAction(@DestinationVariable String classId, WebSocketMessage message) {
        // For now, we just log the received message and broadcast it back.
        // In the future, we can add logic here to save state, check permissions, etc.
        System.out.println("Received action for class " + classId + ": " + message.getType());
        return message;
    }
}