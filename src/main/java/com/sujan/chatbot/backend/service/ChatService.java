package com.sujan.chatbot.backend.service;

import com.sujan.chatbot.backend.dto.request.MessageRequest;
import com.sujan.chatbot.backend.dto.response.ChatSessionResponse;
import com.sujan.chatbot.backend.dto.response.MessageResponse;
import com.sujan.chatbot.backend.model.User;

import java.util.List;

public interface ChatService {
    ChatSessionResponse createChat(User user);

    List<ChatSessionResponse> getAllChats(User user);

    List<MessageResponse> sendMessage(Long chatId, MessageRequest request, User user);

    List<MessageResponse> getMessages(Long chatId, User user);

    void deleteChat(Long chatId, User user);
}
