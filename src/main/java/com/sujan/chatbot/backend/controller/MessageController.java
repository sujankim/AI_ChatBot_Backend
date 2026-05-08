package com.sujan.chatbot.backend.controller;

import com.sujan.chatbot.backend.dto.request.MessageRequest;
import com.sujan.chatbot.backend.dto.response.MessageResponse;
import com.sujan.chatbot.backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats/{chatId}/messages")
@Tag(name = "Messages", description = "Send messages and retrieve chat history")
public class MessageController {

    private final ChatService chatService;

    public MessageController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(
            summary = "Send a message",
            description = "Send a user message to a chat. The bot automatically generates and returns a reply. " +
                    "Returns both the user message and bot reply in the response."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Message sent and bot reply generated"),
            @ApiResponse(responseCode = "400", description = "Message content is blank or invalid"),
            @ApiResponse(responseCode = "404", description = "Chat session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<List<MessageResponse>> sendMessage(
            @Parameter(description = "ID of the chat session", required = true)
            @PathVariable Long chatId,
            @Valid @RequestBody MessageRequest request) {

        List<MessageResponse> messages = chatService.sendMessage(chatId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(messages);
    }

    @Operation(
            summary = "Get chat history",
            description = "Retrieve all messages for a specific chat session, ordered oldest first (chronological)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully retrieved chat history"),
            @ApiResponse(responseCode = "404", description = "Chat session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getMessages(
            @Parameter(description = "ID of the chat session", required = true)
            @PathVariable Long chatId) {

        List<MessageResponse> messages = chatService.getMessages(chatId);
        return ResponseEntity.ok(messages);
    }
}