package com.sujan.chatbot.backend.controller;

import com.sujan.chatbot.backend.dto.ChatSessionResponse;
import com.sujan.chatbot.backend.model.ChatSession;
import com.sujan.chatbot.backend.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatSessionResponse> createChat() {
        ChatSessionResponse response = chatService.createChat();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ChatSessionResponse>> getAllChats() {
        List<ChatSessionResponse> chats = chatService.getAllChats();
        return ResponseEntity.ok(chats);
    }
}
