package com.sujan.chatbot.backend.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String email) {
        super("User with email '" + email + "' was not found");
    }
}
