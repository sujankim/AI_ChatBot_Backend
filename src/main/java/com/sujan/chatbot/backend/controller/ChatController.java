package com.sujan.chatbot.backend.controller;

import com.sujan.chatbot.backend.dto.response.ChatSessionResponse;
import com.sujan.chatbot.backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@Tag(name = "Chat Sessions", description = "Manage chat sessions — create, retrieve, and delete conversations")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Create a new chat session",
            description = "Creates a new chat session with an auto-generated title based on current timestamp"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Chat session created successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ChatSessionResponse> createChat() {
        ChatSessionResponse response = chatService.createChat();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get all chat sessions",
            description = "Returns all chat sessions ordered by creation date, newest first"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all chat sessions"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<ChatSessionResponse>> getAllChats() {
        List<ChatSessionResponse> chats = chatService.getAllChats();
        return ResponseEntity.ok(chats);
    }

    @Operation(
            summary = "Delete a chat session",
            description = "Permanently deletes a chat session and ALL its associated messages"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Chat session deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Chat session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{chatId}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long chatId) {
        chatService.deleteChat(chatId);
        return ResponseEntity.noContent().build();
    }
}