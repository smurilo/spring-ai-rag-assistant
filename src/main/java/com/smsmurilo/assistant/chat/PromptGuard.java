package com.smsmurilo.assistant.chat;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Cheap pre-LLM guardrails: masks personal data before it leaves the service (LGPD)
 * and blocks the most common prompt-injection phrasings.
 */
@Component
public class PromptGuard {

    private static final Pattern CPF = Pattern.compile("\\b\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}\\b");
    private static final Pattern CARD = Pattern.compile("\\b(?:\\d[ -]?){13,16}\\b");
    private static final List<String> INJECTION_MARKERS = List.of(
            "ignore as instruções", "ignore previous instructions", "system prompt", "revele seu prompt");

    public String sanitize(String question) {
        var lower = question.toLowerCase();
        if (INJECTION_MARKERS.stream().anyMatch(lower::contains)) {
            throw new IllegalArgumentException("Pergunta bloqueada pela política de segurança");
        }
        var masked = CARD.matcher(question).replaceAll("[CARTAO]");
        return CPF.matcher(masked).replaceAll("[CPF]");
    }
}
