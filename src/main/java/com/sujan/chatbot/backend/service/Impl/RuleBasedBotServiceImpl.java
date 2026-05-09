package com.sujan.chatbot.backend.service.Impl;

import com.sujan.chatbot.backend.enums.BotIntent;
import com.sujan.chatbot.backend.model.IntentPattern;
import com.sujan.chatbot.backend.service.BotService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
// @Primary removed — GeminiBotServiceImpl is now @Primary
public class RuleBasedBotServiceImpl implements BotService {

    private final List<IntentPattern> intentPatterns;

    public RuleBasedBotServiceImpl() {
        this.intentPatterns = buildIntentPatterns();
    }

    // ─── Core Logic ───────────────────────────────────────────────────────────

    @Override
    public String generateResponse(String userMessage, Long chatId) {
        if (userMessage == null || userMessage.isBlank()) {
            return "I didn't catch that. Could you say something? 😊";
        }

        String cleaned = preprocess(userMessage);

        // Find the intent with the highest confidence
        return intentPatterns.stream()
                .map(ip -> new MatchResult(ip, ip.matches(cleaned)))
                .filter(r -> r.confidence() > 0.0)
                .max(Comparator.comparingDouble(MatchResult::confidence))
                .map(r -> buildResponse(r.intentPattern(), cleaned))
                .orElse(buildUnknownResponse(cleaned));
    }

    // ─── Pre-processing ────────────────────────────────────────────────────────

    /**
     * Clean and normalize user input before matching.
     */
    private String preprocess(String input) {
        return input
                .toLowerCase()
                .trim()
                .replaceAll("\\s+", " ")         // Collapse multiple spaces
                .replaceAll("[!?.,;:]+$", "");    // Remove trailing punctuation
    }

    // ─── Response Building ────────────────────────────────────────────────────

    private String buildResponse(IntentPattern intentPattern, String input) {
        // Special case: ASK_TIME needs current time injected
        if (intentPattern.getIntent() == BotIntent.ASK_TIME) {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"));
            return "The current time is " + time + " 🕐";
        }

        return intentPattern.getNextResponse();
    }

    private String buildUnknownResponse(String input) {
        // Extract key words from the unknown input to make the response feel smarter
        String[] words = input.split("\\s+");
        if (words.length > 0) {
            String topic = words[words.length - 1]; // Last word as "topic"
            return String.format(
                    "I'm not sure I understand '%s' yet. 🤔 " +
                            "I can help with Java, Spring Boot, Angular, MySQL, and AI topics. " +
                            "Type 'help' to see what I can do!",
                    topic
            );
        }
        return "I'm not sure how to respond to that yet. Type 'help' to see what I can do!";
    }

    // ─── Intent Pattern Definitions ───────────────────────────────────────────

    private List<IntentPattern> buildIntentPatterns() {
        return List.of(

                // ── GREETING ──────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.GREETING,
                        List.of(
                                "\\b(hi|hello|hey|howdy|greetings|sup|what'?s up)\\b",
                                "^(good (morning|afternoon|evening|day))"
                        ),
                        List.of(
                                "Hello! 👋 How can I help you today?",
                                "Hey there! 😊 What can I do for you?",
                                "Hi! Great to see you. What's on your mind?",
                                "Hello! I'm here and ready to help. What would you like to know? 🤖"
                        ),
                        0.9
                ),

                // ── FAREWELL ──────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.FAREWELL,
                        List.of(
                                "\\b(bye|goodbye|see you|cya|farewell|take care|later|goodnight)\\b"
                        ),
                        List.of(
                                "Goodbye! 👋 Have a wonderful day!",
                                "See you later! Come back anytime. 😊",
                                "Bye! It was great chatting with you! 🌟",
                                "Take care! I'll be here when you need me. 👋"
                        ),
                        0.9
                ),

                // ── THANKS ────────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.THANKS,
                        List.of(
                                "\\b(thank(s| you)|thx|ty|cheers|appreciate it|grateful)\\b"
                        ),
                        List.of(
                                "You're welcome! 😊 Is there anything else I can help with?",
                                "Happy to help! Let me know if you need anything else. 🌟",
                                "Anytime! Don't hesitate to ask if you have more questions. 😊",
                                "My pleasure! What else can I do for you?"
                        ),
                        0.9
                ),

                // ── HOW ARE YOU ───────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.HOW_ARE_YOU,
                        List.of(
                                "\\b(how are you|how('?re| are) you doing|how('?s| is) it going|you ok|are you ok)\\b"
                        ),
                        List.of(
                                "I'm doing great, thanks for asking! 😊 How can I assist you?",
                                "Running at 100% efficiency! 🤖 How about you? What can I help with?",
                                "Fantastic! Ready to help you learn something new. What's your question?"
                        ),
                        0.95
                ),

                // ── BOT IDENTITY ──────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.BOT_IDENTITY,
                        List.of(
                                "\\b(who|what) are you\\b",
                                "\\byour name\\b",
                                "\\bwhat('?s| is) your name\\b",
                                "\\btell me about yourself\\b",
                                "\\bare you (a |an )?(bot|ai|robot|machine|human)\\b"
                        ),
                        List.of(
                                "I'm an AI Chatbot built with Spring Boot & Angular! 🤖 I'm here to help you learn.",
                                "You can call me ChatBot! I'm powered by Spring Boot on the backend and Angular on the frontend. 💻",
                                "I'm your AI learning assistant! Built with Java 21, Spring Boot 4, and Angular 21. 🚀"
                        ),
                        0.9
                ),

                // ── JAVA ──────────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.ASK_ABOUT_JAVA,
                        List.of(
                                "\\bjava\\b",
                                "\\b(jvm|jdk|jre|maven|gradle)\\b",
                                "\\b(oop|object.?oriented|polymorphism|inheritance|encapsulation)\\b",
                                "\\b(stream|lambda|optional|record|sealed)\\b"
                        ),
                        List.of(
                                "☕ Java is a high-level, object-oriented programming language. " +
                                        "It runs on the JVM, making it platform-independent ('write once, run anywhere'). " +
                                        "Key features: OOP, strong typing, automatic memory management, and a huge ecosystem!",

                                "☕ Java 21 (LTS) brings exciting features: Records for immutable data, " +
                                        "Sealed classes for restricted hierarchies, Pattern matching for switch, " +
                                        "and Virtual Threads for massive concurrency. Which feature interests you most?",

                                "☕ In Java, everything is a class. The JVM compiles your .java files to " +
                                        "bytecode (.class files) that run on any platform. " +
                                        "Want to know about a specific Java concept?"
                        ),
                        0.85
                ),

                // ── SPRING BOOT ───────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.ASK_ABOUT_SPRING,
                        List.of(
                                "\\bspring( boot)?\\b",
                                "\\b(rest|api|endpoint|controller|service|repository|bean|autowired)\\b",
                                "\\b(jpa|hibernate|transaction|entity)\\b",
                                "\\b(dependency injection|ioc|inversion of control)\\b"
                        ),
                        List.of(
                                "🍃 Spring Boot makes building Java backend applications fast and easy! " +
                                        "It uses convention over configuration — most things work out of the box. " +
                                        "Key features: Dependency Injection, Auto-configuration, Embedded servers, and Production-ready metrics.",

                                "🍃 Spring Boot follows a layered architecture: " +
                                        "Controller (HTTP) → Service (business logic) → Repository (database). " +
                                        "Each layer has a single responsibility. Which layer do you want to learn more about?",

                                "🍃 Spring Data JPA lets you query databases without writing SQL! " +
                                        "Just define method names like findByTitleOrderByCreatedAtDesc() " +
                                        "and Spring generates the query automatically. Magic! ✨"
                        ),
                        0.85
                ),

                // ── ANGULAR ───────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.ASK_ABOUT_ANGULAR,
                        List.of(
                                "\\bangular\\b",
                                "\\b(typescript|component|directive|pipe|service|module|signal)\\b",
                                "\\b(rxjs|observable|subscribe|async)\\b",
                                "\\b(ngfor|ngif|two.?way binding|data binding)\\b"
                        ),
                        List.of(
                                "🅰️ Angular is a TypeScript-based framework by Google for building web apps. " +
                                        "Key concepts: Components (UI blocks), Services (business logic), " +
                                        "Signals (reactive state), and Directives (DOM manipulation). " +
                                        "Angular 21 is the version we're using in this project!",

                                "🅰️ Angular Signals are the modern way to manage reactive state. " +
                                        "A signal is a value that Angular watches — when it changes, " +
                                        "only the parts of the template that use it re-render. " +
                                        "Much more efficient than Angular's old change detection!",

                                "🅰️ In Angular, every component has 3 files: " +
                                        ".ts (brain/logic), .html (template/face), .scss (styles/appearance). " +
                                        "The @Component decorator links them together. Which part do you want to explore?"
                        ),
                        0.85
                ),

                // ── MYSQL ─────────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.ASK_ABOUT_MYSQL,
                        List.of(
                                "\\bmysql\\b",
                                "\\b(sql|database|table|query|select|insert|update|delete|join)\\b",
                                "\\b(primary key|foreign key|index|normalization|schema)\\b"
                        ),
                        List.of(
                                "🗄️ MySQL is a relational database management system. " +
                                        "Data is organized into tables with rows and columns. " +
                                        "Tables relate to each other via foreign keys. " +
                                        "In our project: chat_sessions and messages tables are linked!",

                                "🗄️ SQL (Structured Query Language) is how you talk to MySQL. " +
                                        "The 4 core operations: SELECT (read), INSERT (create), " +
                                        "UPDATE (modify), DELETE (remove). " +
                                        "Spring Data JPA generates these for you from method names!",

                                "🗄️ In our chatbot database: " +
                                        "chat_sessions stores each conversation. " +
                                        "messages stores each individual message with a foreign key to chat_sessions. " +
                                        "This is a one-to-many relationship: one chat → many messages!"
                        ),
                        0.85
                ),

                // ── AI ────────────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.ASK_ABOUT_AI,
                        List.of(
                                "\\b(artificial intelligence|machine learning|deep learning|neural network)\\b",
                                "\\b(llm|large language model|gpt|gemini|chatgpt)\\b",
                                "\\b(nlp|natural language|sentiment|classification)\\b",
                                "\\bhow do you work\\b"
                        ),
                        List.of(
                                "🧠 AI (Artificial Intelligence) is the simulation of human intelligence by machines. " +
                                        "This chatbot uses rule-based NLP — matching patterns in your text to intents. " +
                                        "Phase 8 of our project upgrades this to a real LLM (Google Gemini)!",

                                "🧠 Large Language Models (LLMs) like GPT and Gemini are trained on " +
                                        "billions of text examples. They predict the most likely next word, " +
                                        "generating human-like responses. They don't 'understand' — they pattern-match at massive scale!",

                                "🧠 I currently use Rule-Based NLP: I match your message against " +
                                        "regex patterns for known intents, score confidence, and pick the best match. " +
                                        "Simple but surprisingly effective for focused domains!"
                        ),
                        0.85
                ),

                // ── HELP ──────────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.ASK_HELP,
                        List.of(
                                "\\b(help|what can you do|commands|capabilities|features)\\b"
                        ),
                        List.of(
                                "🆘 Here's what I can help you with:\n\n" +
                                        "💬 **Conversation**: Greetings, how are you, goodbye\n" +
                                        "☕ **Java**: Language features, JVM, OOP concepts\n" +
                                        "🍃 **Spring Boot**: REST API, JPA, dependency injection\n" +
                                        "🅰️ **Angular**: Components, signals, TypeScript, RxJS\n" +
                                        "🗄️ **MySQL**: SQL queries, relationships, schema design\n" +
                                        "🧠 **AI**: How AI works, LLMs, NLP concepts\n" +
                                        "🕐 **Time**: Ask me what time it is\n" +
                                        "😄 **Jokes**: Programmer humor!\n\n" +
                                        "Just ask me anything in those categories!"
                        ),
                        0.9
                ),

                // ── JOKE ──────────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.ASK_JOKE,
                        List.of(
                                "\\b(joke|funny|laugh|humor|pun|tell me a joke|make me laugh)\\b"
                        ),
                        List.of(
                                "Why do programmers prefer dark mode? Because light attracts bugs! 🐛😄",
                                "Why did the developer go broke? Because he used up all his cache! 💸😄",
                                "How many programmers does it take to change a light bulb? None — that's a hardware problem! 💡😄",
                                "A SQL query walks into a bar, walks up to two tables and asks... 'Can I join you?' 😄",
                                "Why do Java developers wear glasses? Because they don't see sharp! 👓😄",
                                "What do you call a programmer from Finland? Nerdic! 🧊😄"
                        ),
                        0.9
                ),

                // ── ASK TIME ──────────────────────────────────────────────────────
                new IntentPattern(
                        BotIntent.ASK_TIME,
                        List.of(
                                "\\b(what time|current time|what'?s the time|time is it)\\b"
                        ),
                        List.of("TIME_RESPONSE"), // Placeholder — overridden in buildResponse()
                        0.95
                )
        );
    }

    // ─── Helper Record ────────────────────────────────────────────────────────

    /**
     * Holds an intent pattern + its confidence score for a given input.
     * Java record: immutable data carrier (like a DTO for internal use).
     */
    private record MatchResult(IntentPattern intentPattern, double confidence) {}
}
