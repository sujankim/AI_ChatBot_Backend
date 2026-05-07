package com.sujan.chatbot.backend.service;

import com.sujan.chatbot.backend.dto.ChatSessionResponse;

import java.util.List;

public interface ChatService {
    /**
     * Create a new chat session.
     * Returns the created session as a response DTO.
     */
    ChatSessionResponse createChat();

    /**
     * Retrieve all chat sessions, ordered by creation date (newest first).
     */
    List<ChatSessionResponse> getAllChats();
}
