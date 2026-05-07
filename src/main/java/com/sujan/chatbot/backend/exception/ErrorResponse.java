package com.sujan.chatbot.backend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class ErrorResponse {

    private int status;
    private String error;
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    // Constructor — you'll always build it with all fields
    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();  // Always set automatically
    }

    // Getters (Jackson needs these to serialize to JSON)
    public int getStatus()           { return status; }
    public String getError()         { return error; }
    public String getMessage()       { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
