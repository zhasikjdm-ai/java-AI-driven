package com.ai.driven.service;

import com.ai.driven.client.GroqAiClient;
import com.ai.driven.config.AiProperties;
import com.ai.driven.dto.AiRequest;
import com.ai.driven.dto.AiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Сервис бизнес-логики для обработки AI-запросов.
 *
 * Отвечает за:
 * - координацию между Controller и GroqAiClient
 * - измерение времени обработки запроса
 * - логирование операций
 * - формирование итогового DTO-ответа
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final GroqAiClient groqAiClient;
    private final AiProperties aiProperties;

    /**
     * Обрабатывает запрос пользователя:
     * 1. Логирует входящий запрос
     * 2. Замеряет время выполнения
     * 3. Делегирует запрос к GroqAiClient
     * 4. Формирует AiResponse с результатом
     *
     * @param request DTO с вопросом пользователя
     * @return AiResponse с ответом AI и метаданными
     */
    public AiResponse processRequest(AiRequest request) {
        log.info("Получен запрос к AI. Сообщение: '{}'",
                truncate(request.getMessage(), 100));

        // Запоминаем время начала для расчёта времени обработки
        long startTime = System.currentTimeMillis();

        // Отправляем запрос в AI через клиент
        String aiAnswer = groqAiClient.sendMessage(request.getMessage());

        // Считаем время обработки
        long processingTimeMs = System.currentTimeMillis() - startTime;

        log.info("Запрос обработан за {} мс. Длина ответа: {} символов",
                processingTimeMs, aiAnswer.length());

        // Формируем успешный ответ
        return AiResponse.success(aiAnswer, aiProperties.getModel(), processingTimeMs);
    }

    /**
     * Обрезает строку до maxLength символов для безопасного логирования.
     * Добавляет "..." если строка была обрезана.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "null";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
}
