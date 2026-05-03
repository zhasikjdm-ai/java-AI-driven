package com.ai.driven.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для входящего запроса от клиента.
 * Содержит текстовое сообщение пользователя,
 * которое будет отправлено в AI-модель.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {

    /**
     * Текст вопроса пользователя.
     * Не может быть пустым, минимум 2 символа, максимум 5000.
     */
    @NotBlank(message = "Сообщение не может быть пустым")
    @Size(min = 2, max = 5000, message = "Длина сообщения должна быть от 2 до 5000 символов")
    private String message;
}
