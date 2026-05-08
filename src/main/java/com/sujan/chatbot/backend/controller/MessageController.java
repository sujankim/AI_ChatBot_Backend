package com.sujan.chatbot.backend.controller;

import com.sujan.chatbot.backend.dto.request.MessageRequest;
import com.sujan.chatbot.backend.dto.response.MessageResponse;
import com.sujan.chatbot.backend.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats/{chatId}/messages")
public class MessageController {

    private final ChatService chatService;

    public MessageController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Send a message to a chat and receive bot reply.
     * Returns [userMessage, botMessage] — both in one response.
     */
    @PostMapping
    public ResponseEntity<List<MessageResponse>> sendMessage(
            @PathVariable Long chatId,
            @Valid @RequestBody MessageRequest request) {

        List<MessageResponse> messages = chatService.sendMessage(chatId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(messages);
    }

    /**
     * Get all messages for a chat, oldest first.
     */
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long chatId) {

        List<MessageResponse> messages = chatService.getMessages(chatId);
        return ResponseEntity.ok(messages);
    }
}
