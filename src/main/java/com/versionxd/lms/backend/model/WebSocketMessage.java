package com.versionxd.lms.backend.model;

import lombok.Data;

@Data
public class WebSocketMessage {
    private String type; // e.g., "DRAW", "CODE_CHANGE", "USER_JOIN"
    private String sender;
    private Object content; // Can hold any data (coordinates, text, etc.)
}