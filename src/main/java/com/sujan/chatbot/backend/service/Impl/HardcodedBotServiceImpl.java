package com.sujan.chatbot.backend.service.Impl;

import com.sujan.chatbot.backend.service.BotService;
import org.springframework.stereotype.Service;

@Service
public class HardcodedBotServiceImpl implements BotService {

    @Override
    public String generateResponse(String userMessage) {
        // Normalize: lowercase + trim whitespace
        String input = userMessage.toLowerCase().trim();

        // ─── Greetings ────────────────────────────────────────────────
        if (input.contains("hello") || input.contains("hi") || input.contains("hey")) {
            return "Hello! 👋 How can I help you today?";
        }

        // ─── Wellbeing ────────────────────────────────────────────────
        if (input.contains("how are you")) {
            return "I'm doing great, thanks for asking! 😊 How can I assist you?";
        }

        // ─── Identity ─────────────────────────────────────────────────
        if (input.contains("who are you") || input.contains("what are you")) {
            return "I'm an AI Chatbot built with Spring Boot & Angular! 🤖";
        }

        if (input.contains("your name")) {
            return "You can call me ChatBot! What can I do for you?";
        }

        // ─── Help ─────────────────────────────────────────────────────
        if (input.contains("help")) {
            return "I can respond to: greetings, 'how are you', " +
                    "'who are you', 'thank you', and 'bye'. Try them out!";
        }

        // ─── Gratitude ────────────────────────────────────────────────
        if (input.contains("thank")) {
            return "You're welcome! 😊 Is there anything else I can help with?";
        }

        // ─── Farewell ─────────────────────────────────────────────────
        if (input.contains("bye") || input.contains("goodbye")) {
            return "Goodbye! 👋 Have a wonderful day!";
        }

        // ─── Jokes ────────────────────────────────────────────────────
        if (input.contains("joke")) {
            return "Why do programmers prefer dark mode? " +
                    "Because light attracts bugs! 🐛😄";
        }

        // ─── Default ──────────────────────────────────────────────────
        return "I'm not sure I understand that yet. " +
                "Type 'help' to see what I can respond to!";
    }
}