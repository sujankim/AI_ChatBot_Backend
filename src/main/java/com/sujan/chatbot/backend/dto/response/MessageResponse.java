package com.sujan.chatbot.backend.dto.response;

import com.sujan.chatbot.backend.enums.SenderType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private Long id;
    private String content;
    private SenderType senderType;    // USER or BOT
    private LocalDateTime createdAt;
}
