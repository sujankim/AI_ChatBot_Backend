package com.sujan.chatbot.backend.service.Impl;

import com.sujan.chatbot.backend.dto.ChatSessionResponse;
import com.sujan.chatbot.backend.mapper.ChatSessionMapper;
import com.sujan.chatbot.backend.model.ChatSession;
import com.sujan.chatbot.backend.repository.ChatSessionRepository;
import com.sujan.chatbot.backend.service.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatSessionMapper chatSessionMapper;

    public ChatServiceImpl(ChatSessionRepository chatSessionRepository,
                           ChatSessionMapper chatSessionMapper) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatSessionMapper = chatSessionMapper;
    }

    @Override
    @Transactional
    public ChatSessionResponse createChat(){
        // 1. Generate a smart default title based on current time
        String title = generateDefaultTitle();

        // 2. Build the entity
        ChatSession chatSession = new ChatSession();
        chatSession.setTitle(title);

        // 3. Persist to database (save() returns the saved entity with id + timestamps)
        ChatSession savedSession = chatSessionRepository.save(chatSession);

        // 4. Convert to DTO and return
        return chatSessionMapper.toResponse(savedSession);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSessionResponse> getAllChats() {
        // Implementation coming in the next vertical slice (Phase 3)
        // For now, return empty list as a safe placeholder
        return List.of();
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    /**
     * Generates a human-friendly default title like "New Chat — May 7, 2026 14:32"
     */
    private String generateDefaultTitle() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");
        return "New Chat — " + LocalDateTime.now().format(formatter);
    }
}
