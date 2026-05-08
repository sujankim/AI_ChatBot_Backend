package com.sujan.chatbot.backend.mapper;

import com.sujan.chatbot.backend.dto.response.MessageResponse;
import com.sujan.chatbot.backend.model.Message;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    MessageResponse toResponse(Message entity);

    List<MessageResponse> toResponseList(List<Message> entities);
}
