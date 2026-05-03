package com.ai.driven.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Модель запроса к Groq/OpenAI совместимому API.
 * Отправляется как JSON-тело POST-запроса к AI-провайдеру.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroqRequest {

    /** Название AI-модели (например: llama-3.1-70b-versatile) */
    private String model;

    /** Список сообщений в формате чата */
    private List<Message> messages;

    /** Максимальное количество токенов в ответе */
    @JsonProperty("max_tokens")
    private Integer maxTokens;

    /** Температура генерации (0.0 - 2.0): выше = более творческий ответ */
    private Double temperature;

    /**
     * Внутренний класс для представления одного сообщения в чате.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {

        /**
         * Роль отправителя:
         * - "system"    — системный промпт (инструкция AI)
         * - "user"      — сообщение пользователя
         * - "assistant" — ответ AI
         */
        private String role;

        /** Текст сообщения */
        private String content;
    }
}
