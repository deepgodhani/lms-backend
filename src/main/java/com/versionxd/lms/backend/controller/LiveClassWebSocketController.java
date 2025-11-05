package com.versionxd.lms.backend.controller;

import com.versionxd.lms.backend.model.WebSocketMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class LiveClassWebSocketController {

    /**
     * This method handles all actions sent to "/app/live-class/{classId}".
     * It simply broadcasts the message to all subscribers of "/topic/live-class/{classId}".
     * The frontend logic will decide what to do with the message based on its 'type'.
     */
    @MessageMapping("/live-class/{classId}")
    @SendTo("/topic/live-class/{classId}")
    public WebSocketMessage handleClassAction(@DestinationVariable String classId, WebSocketMessage message) {

        System.out.println("Received action for class " + classId + ": " + message.getType());

        // Broadcast the message (DRAW, CLEAR_CANVAS, CODE_UPDATE, etc.)
        return message;
    }
}