package com.ai.driven.client;

import com.ai.driven.config.AiProperties;
import com.ai.driven.exception.AiApiException;
import com.ai.driven.exception.AiTimeoutException;
import com.ai.driven.model.GroqRequest;
import com.ai.driven.model.GroqResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * HTTP-клиент для взаимодействия с Groq AI API.
 *
 * Отвечает за:
 * - формирование запроса к API в формате OpenAI Chat Completions
 * - отправку HTTP POST запроса с Bearer-токеном
 * - получение и десериализацию ответа
 * - обработку HTTP-ошибок и таймаутов
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroqAiClient {

    private final WebClient webClient;
    private final AiProperties aiProperties;

    /**
     * Системный промпт — задаёт поведение AI-модели.
     * Можно вынести в application.yml для гибкости.
     */
    private static final String SYSTEM_PROMPT = """
            Ты — умный и полезный AI-ассистент. Отвечай на вопросы пользователей чётко, 
            грамотно и развёрнуто. Используй язык вопроса для ответа. Если вопрос на русском — 
            отвечай на русском. Будь точным, дружелюбным и профессиональным.
            """;

    /**
     * Отправляет сообщение пользователя в Groq API и возвращает ответ модели.
     *
     * @param userMessage текст вопроса пользователя
     * @return ответ от AI-модели
     * @throws AiApiException     при HTTP-ошибке от API
     * @throws AiTimeoutException при превышении таймаута
     */
    public String sendMessage(String userMessage) {
        log.info("Отправка запроса к Groq API. Модель: {}, Длина сообщения: {} символов",
                aiProperties.getModel(), userMessage.length());

        // Формируем тело запроса в формате OpenAI Chat Completions
        GroqRequest request = buildRequest(userMessage);

        try {
            GroqResponse response = webClient.post()
                    .uri(aiProperties.getUrl())
                    // Bearer-авторизация через API-ключ
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + aiProperties.getKey())
                    .bodyValue(request)
                    .retrieve()
                    // Обработка HTTP 4xx ошибок
                    .onStatus(status -> status.is4xxClientError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new AiApiException(
                                            "Ошибка клиента AI API [" + clientResponse.statusCode().value() + "]: " + body,
                                            clientResponse.statusCode().value()
                                    ))
                    )
                    // Обработка HTTP 5xx ошибок
                    .onStatus(status -> status.is5xxServerError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> new AiApiException(
                                            "Ошибка сервера AI API [" + clientResponse.statusCode().value() + "]: " + body,
                                            clientResponse.statusCode().value()
                                    ))
                    )
                    .bodyToMono(GroqResponse.class)
                    .block(); // Блокирующий вызов (синхронный режим)

            // Извлекаем текст ответа из структуры GroqResponse
            String content = response != null ? response.extractContent() : null;

            if (content == null || content.isBlank()) {
                throw new AiApiException("AI API вернул пустой ответ", 502);
            }

            log.info("Ответ от Groq API получен. Токенов использовано: {}",
                    response.getUsage() != null ? response.getUsage().getTotalTokens() : "N/A");

            return content;

        } catch (AiApiException e) {
            // Прокидываем наше исключение дальше без оборачивания
            throw e;
        } catch (WebClientRequestException e) {
            // Ошибка соединения или таймаут на уровне TCP
            if (e.getCause() instanceof TimeoutException) {
                log.error("Таймаут подключения к Groq API: {}", e.getMessage());
                throw new AiTimeoutException("Превышено время ожидания ответа от AI API", e);
            }
            log.error("Ошибка соединения с Groq API: {}", e.getMessage());
            throw new AiApiException("Не удалось подключиться к AI API: " + e.getMessage(), e);
        } catch (WebClientResponseException e) {
            log.error("HTTP ошибка от Groq API: {} - {}", e.getStatusCode(), e.getMessage());
            throw new AiApiException(
                    "AI API вернул ошибку: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(),
                    e.getStatusCode().value()
            );
        } catch (Exception e) {
            log.error("Неожиданная ошибка при вызове Groq API: {}", e.getMessage(), e);
            throw new AiApiException("Ошибка при обращении к AI API: " + e.getMessage(), e);
        }
    }

    /**
     * Формирует запрос к AI API с системным промптом и сообщением пользователя.
     *
     * @param userMessage текст вопроса пользователя
     * @return готовый объект запроса
     */
    private GroqRequest buildRequest(String userMessage) {
        return GroqRequest.builder()
                .model(aiProperties.getModel())
                .maxTokens(aiProperties.getMaxTokens())
                .temperature(aiProperties.getTemperature())
                .messages(List.of(
                        // Системный промпт — инструкция для AI
                        GroqRequest.Message.builder()
                                .role("system")
                                .content(SYSTEM_PROMPT)
                                .build(),
                        // Сообщение пользователя
                        GroqRequest.Message.builder()
                                .role("user")
                                .content(userMessage)
                                .build()
                ))
                .build();
    }
}
