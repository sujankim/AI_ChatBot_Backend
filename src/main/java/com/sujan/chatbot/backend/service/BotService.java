package com.sujan.chatbot.backend.service;

public interface BotService {
    /**
     * Generate a bot reply to the given user message.
     *
     * @param userMessage the message typed by the user
     * @return the bot's response text
     */
    String generateResponse(String userMessage, Long chatId);
}
