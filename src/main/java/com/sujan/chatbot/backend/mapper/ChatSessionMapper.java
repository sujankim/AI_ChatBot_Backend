package com.sujan.chatbot.backend.mapper;

import com.sujan.chatbot.backend.dto.ChatSessionResponse;
import com.sujan.chatbot.backend.model.ChatSession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatSessionMapper {

    /**
     * Convert a single ChatSession entity to a ChatSessionResponse DTO.
     */
    public ChatSessionResponse toResponse(ChatSession entity) {
        if (entity == null) {
            return null;
        }

        return ChatSessionResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convert a list of ChatSession entities to a list of ChatSessionResponse DTOs.
     */
    public List<ChatSessionResponse> toResponseList(List<ChatSession> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
