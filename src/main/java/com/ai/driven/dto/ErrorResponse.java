package com.ai.driven.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Универсальный DTO для ответов с ошибками.
 * Используется глобальным обработчиком исключений.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /** HTTP-статус код */
    private int status;

    /** Краткое описание ошибки */
    private String error;

    /** Подробное сообщение */
    private String message;

    /** Путь запроса */
    private String path;

    /** Временная метка */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /** Детали валидации (поле → сообщение об ошибке) */
    private Map<String, String> validationErrors;
}
