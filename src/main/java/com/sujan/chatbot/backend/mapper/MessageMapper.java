package com.sujan.chatbot.backend.mapper;

import com.sujan.chatbot.backend.dto.response.MessageResponse;
import com.sujan.chatbot.backend.model.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MessageMapper {

    public MessageResponse toResponse(Message entity){

    if(entity == null){
        return null;
    }
    return MessageResponse.builder()
            .id(entity.getId())
            .content(entity.getContent())
            .senderType(entity.getSenderType())
            .createdAt(entity.getCreatedAt())
            .build();
    }

    public List<MessageResponse> toResponseList(List<Message> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toResponse)
                .toList();
    }
}
