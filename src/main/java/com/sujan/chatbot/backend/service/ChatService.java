package com.sujan.chatbot.backend.service;

import com.sujan.chatbot.backend.dto.ChatSessionResponse;
import com.sujan.chatbot.backend.dto.request.MessageRequest;
import com.sujan.chatbot.backend.dto.response.MessageResponse;

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

    /**
     * Send a user message and get bot reply.
     * Returns [userMessage, botMessage] — both saved to DB.
     */
    List<MessageResponse> sendMessage(Long chatId, MessageRequest request);

    /**
     * Get all messages for a chat, oldest first.
     */
    List<MessageResponse> getMessages(Long chatId);

    /**
     * Deletes a chat session and all its associated messages.
     */
    void deleteChat(Long chatId);
}
