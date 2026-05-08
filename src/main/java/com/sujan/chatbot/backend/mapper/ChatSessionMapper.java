package com.sujan.chatbot.backend.mapper;

import com.sujan.chatbot.backend.dto.response.ChatSessionResponse;
import com.sujan.chatbot.backend.model.ChatSession;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatSessionMapper {

    ChatSessionResponse toResponse(ChatSession entity);

    List<ChatSessionResponse> toResponseList(List<ChatSession> entities);
}
