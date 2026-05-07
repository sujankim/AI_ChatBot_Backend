package com.sujan.chatbot.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // ─── 1. Business Exceptions ───────────────────────────────────────────────

    /**
     * Handles: Chat session not found in database
     * Triggered by: chatSessionRepository.findById() returning empty
     */
    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleChatNotFound(ChatNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),   // 404
                "CHAT_NOT_FOUND",
                ex.getMessage()                 // "Chat session with id 99 was not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ─── 2. Spring MVC Exceptions ─────────────────────────────────────────────

    /**
     * Handles: URL path doesn't match any controller
     * Triggered by: Calling /api/chat instead of /api/chats (sound familiar? 😄)
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "ENDPOINT_NOT_FOUND",
                "The requested endpoint does not exist"  // Safe — no internals leaked
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ─── 3. Catch-All Safety Net ──────────────────────────────────────────────

    /**
     * Handles: Anything we didn't anticipate
     * Triggered by: Any unhandled exception
     * Rule: NEVER expose the real error message to the client
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // ⚠️ Log the REAL error for developers (we'll add proper logging later)
        System.err.println("Unexpected error: " + ex.getMessage());

        // Return a SAFE message to the client — no internals
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),  // 500
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please try again later."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
