package com.sujan.chatbot.backend.service.Impl;

import com.sujan.chatbot.backend.service.BotService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class GeminiBotServiceImpl implements BotService {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    /**
     * Spring AI auto-configures a ChatClient.Builder bean based on
     * the spring.ai.google.genai.* properties in application.properties.
     * We use it here to build our configured ChatClient.
     */
    public GeminiBotServiceImpl(ChatClient.Builder chatClientBuilder) {

        // ─── 1. Create the memory store ───────────────────────────────────────
        // InMemoryChatMemoryRepository: stores all conversations in RAM
        // MessageWindowChatMemory: keeps last 20 messages per conversation
        // Each chatId gets its own isolated window
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(20)    // Keep last 20 messages (10 exchanges)
                .build();

        // ─── 2. Build ChatClient with memory advisor ──────────────────────────
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are a helpful AI assistant integrated into a learning chatbot.
                        You help developers learn Java, Spring Boot, Angular, MySQL, and
                        general programming concepts.
                        Be friendly, clear, and concise.
                        Use code examples when they add clarity.
                        Only answer technology and programming related questions.
                        If asked about something unrelated to tech, politely redirect
                        to programming topics.
                        """)
                .defaultAdvisors(
                        // Register the memory advisor — it intercepts every call:
                        // BEFORE: loads history for this chatId → adds to prompt
                        // AFTER:  saves user + assistant messages to memory
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @Override
    public String generateResponse(String userMessage, Long chatId) {
        if (userMessage == null || userMessage.isBlank()) {
            return "I didn't catch that. Could you say something? 😊";
        }

        try {
            return chatClient
                    .prompt()
                    .user(userMessage)
                    .advisors(advisorSpec -> advisorSpec
                            // This is the KEY — pass chatId as the memory identifier
                            // Each chatId has a completely separate memory window
                            // Chat 1 and Chat 2 never share context
                            .param(ChatMemory.CONVERSATION_ID, chatId.toString())
                    )
                    .call()
                    .content();

        } catch (Exception ex) {
            System.err.println("Gemini API error: " + ex.getMessage());
            return "I'm having trouble connecting to my AI brain right now. " +
                    "Please try again in a moment! 🔄";
        }
    }

    /**
     * Clears the AI memory for a specific chat.
     * Called when a chat is deleted so we don't keep orphaned memory.
     */
    public void clearMemory(Long chatId) {
        chatMemory.clear(chatId.toString());
    }
}
