package com.sujan.chatbot.backend.service.Impl;

import com.sujan.chatbot.backend.dto.response.ChatSessionResponse;
import com.sujan.chatbot.backend.dto.request.MessageRequest;
import com.sujan.chatbot.backend.dto.response.MessageResponse;
import com.sujan.chatbot.backend.enums.SenderType;
import com.sujan.chatbot.backend.exception.ChatNotFoundException;
import com.sujan.chatbot.backend.mapper.ChatSessionMapper;
import com.sujan.chatbot.backend.mapper.MessageMapper;
import com.sujan.chatbot.backend.model.ChatSession;
import com.sujan.chatbot.backend.model.Message;
import com.sujan.chatbot.backend.repository.ChatSessionRepository;
import com.sujan.chatbot.backend.repository.MessageRepository;
import com.sujan.chatbot.backend.service.BotService;
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
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;
    private final BotService botService;
    private final GeminiBotServiceImpl geminiBotService;

    public ChatServiceImpl(ChatSessionRepository chatSessionRepository,
                           ChatSessionMapper chatSessionMapper,
                           MessageMapper messageMapper,
                           MessageRepository messageRepository, BotService botService, GeminiBotServiceImpl geminiBotService) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatSessionMapper = chatSessionMapper;
        this.messageMapper = messageMapper;
        this.messageRepository = messageRepository;
        this.botService = botService;
        this.geminiBotService = geminiBotService;
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
        List<ChatSession> sessions = chatSessionRepository.findAllByOrderByCreatedAtDesc();
        return chatSessionMapper.toResponseList(sessions);
    }

    @Override
    @Transactional
    public List<MessageResponse> sendMessage(Long chatId, MessageRequest request) {
        // 1. Find the chat session
        ChatSession chatSession = chatSessionRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        // 2. Save the USER message
        Message userMessage = new Message();
        userMessage.setContent(request.getContent());
        userMessage.setSenderType(SenderType.USER);
        userMessage.setChatSession(chatSession);
        Message savedUserMessage = messageRepository.save(userMessage);

        // 3. Generate bot response — NOW PASSES chatId for memory context
        String botReply = botService.generateResponse(request.getContent(), chatId);

        // 4. Save the BOT message
        Message botMessage = new Message();
        botMessage.setContent(botReply);
        botMessage.setSenderType(SenderType.BOT);
        botMessage.setChatSession(chatSession);
        Message savedBotMessage = messageRepository.save(botMessage);

        // 5. Return both messages as DTOs
        return List.of(
                messageMapper.toResponse(savedUserMessage),
                messageMapper.toResponse(savedBotMessage)
        );
    }


    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> getMessages(Long chatId) {
        // Verify chat exists first
        if(!chatSessionRepository.existsById(chatId)){
            throw new ChatNotFoundException(chatId);
        }

        List<Message> messages =
                messageRepository.findByChatSessionIdOrderByCreatedAtAsc(chatId);
        return messageMapper.toResponseList(messages);
    }

    @Override
    @Transactional
    public void deleteChat(Long chatId) {
        // 1. Verify the chat session exists
        ChatSession chatSession= chatSessionRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException(chatId));

        // 2. Find all messages associated with the chat
        List<Message> messages =
                messageRepository.findByChatSessionIdOrderByCreatedAtAsc(chatId);

        // 3. Delete all the messages
        if(!messages.isEmpty()){
            messageRepository.deleteAll(messages);
        }

        // 4. Delete the chat session itself
        chatSessionRepository.delete(chatSession);

        // Clear AI memory for this chat — no orphaned context left in RAM
        geminiBotService.clearMemory(chatId);
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
