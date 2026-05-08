package com.sujan.chatbot.backend.enums;

public enum BotIntent {
    // Conversational
    GREETING,
    FAREWELL,
    THANKS,
    HOW_ARE_YOU,
    BOT_IDENTITY,

    // Tech Topics (for our chatbot's domain)
    ASK_ABOUT_JAVA,
    ASK_ABOUT_SPRING,
    ASK_ABOUT_ANGULAR,
    ASK_ABOUT_MYSQL,
    ASK_ABOUT_AI,

    // Utility
    ASK_HELP,
    ASK_JOKE,
    ASK_TIME,

    // Fallback
    UNKNOWN
}
