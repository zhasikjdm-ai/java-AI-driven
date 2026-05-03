package com.ai.driven.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO для ответа клиенту.
 * Содержит статус операции и текст ответа от AI-модели.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AiResponse {

    /** Статус выполнения запроса */
    private boolean success;

    /** Текст ответа от AI-модели */
    private String response;

    /** Использованная AI-модель */
    private String model;

    /** Время обработки запроса (мс) */
    private Long processingTimeMs;

    /** Временная метка ответа */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Быстрое создание успешного ответа.
     */
    public static AiResponse success(String response, String model, long processingTimeMs) {
        return AiResponse.builder()
                .success(true)
                .response(response)
                .model(model)
                .processingTimeMs(processingTimeMs)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Быстрое создание ответа с ошибкой.
     */
    public static AiResponse error(String errorMessage) {
        return AiResponse.builder()
                .success(false)
                .response(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
