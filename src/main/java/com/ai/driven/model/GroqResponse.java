package com.ai.driven.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Модель ответа от Groq/OpenAI совместимого API.
 * Десериализуется из JSON-тела ответа AI-провайдера.
 *
 * Пример структуры ответа:
 * {
 *   "id": "chatcmpl-xxx",
 *   "model": "llama-3.1-70b-versatile",
 *   "choices": [{
 *     "index": 0,
 *     "message": { "role": "assistant", "content": "..." },
 *     "finish_reason": "stop"
 *   }],
 *   "usage": { "prompt_tokens": 10, "completion_tokens": 100, "total_tokens": 110 }
 * }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroqResponse {

    /** Уникальный ID завершения */
    private String id;

    /** Использованная модель */
    private String model;

    /** Список вариантов ответа (обычно один) */
    private List<Choice> choices;

    /** Статистика использования токенов */
    private Usage usage;

    /**
     * Возвращает текст первого ответа от AI или null.
     */
    public String extractContent() {
        if (choices != null && !choices.isEmpty()) {
            Choice firstChoice = choices.get(0);
            if (firstChoice.getMessage() != null) {
                return firstChoice.getMessage().getContent();
            }
        }
        return null;
    }

    // ── Вложенные классы ──────────────────────────────────────────────────────

    /**
     * Один вариант ответа от модели.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Choice {

        /** Порядковый номер */
        private int index;

        /** Сообщение-ответ от AI */
        private GroqRequest.Message message;

        /** Причина завершения: "stop", "length", "content_filter" */
        @JsonProperty("finish_reason")
        private String finishReason;
    }

    /**
     * Статистика использования токенов.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Usage {

        @JsonProperty("prompt_tokens")
        private int promptTokens;

        @JsonProperty("completion_tokens")
        private int completionTokens;

        @JsonProperty("total_tokens")
        private int totalTokens;
    }
}
