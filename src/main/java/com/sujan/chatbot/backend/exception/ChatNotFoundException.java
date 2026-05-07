package com.sujan.chatbot.backend.exception;

public class ChatNotFoundException extends RuntimeException {
    private final Long chatId;

    public ChatNotFoundException(Long chatId) {
        super("Chat session with id " + chatId + " was not found");
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }
}
