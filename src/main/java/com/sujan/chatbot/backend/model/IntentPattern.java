package com.sujan.chatbot.backend.model;

import com.sujan.chatbot.backend.enums.BotIntent;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents one intent with its matching patterns and possible responses.
 * This is NOT a JPA entity — it's a plain Java object used only by BotService.
 */
public class IntentPattern {

    private final BotIntent intent;
    private final List<Pattern> patterns;   // Compiled regex patterns
    private final List<String> responses;   // Possible responses (rotated)
    private final double baseConfidence;    // How confident when matched (0.0 - 1.0)

    private int responseIndex = 0;          // Tracks which response to use next

    public IntentPattern(BotIntent intent,
                         List<String> regexPatterns,
                         List<String> responses,
                         double baseConfidence) {
        this.intent = intent;
        this.patterns = regexPatterns.stream()
                .map(p -> Pattern.compile(p, Pattern.CASE_INSENSITIVE))
                .toList();
        this.responses = responses;
        this.baseConfidence = baseConfidence;
    }

    /**
     * Check if the input matches any pattern for this intent.
     * Returns confidence score (0.0 = no match, > 0.0 = match).
     */
    public double matches(String input) {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(input).find()) {
                return baseConfidence;
            }
        }
        return 0.0;
    }

    /**
     * Get the next response, rotating through available responses.
     * Prevents the bot from always giving the same answer.
     */
    public String getNextResponse() {
        String response = responses.get(responseIndex);
        responseIndex = (responseIndex + 1) % responses.size(); // Wrap around
        return response;
    }

    public BotIntent getIntent() {
        return intent;
    }
}