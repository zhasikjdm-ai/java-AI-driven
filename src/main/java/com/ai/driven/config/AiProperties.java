package com.ai.driven.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Класс конфигурации AI API.
 * Читает значения из application.yml (блок ai.api.*).
 *
 * Пример в application.yml:
 * ai:
 *   api:
 *     url: https://api.groq.com/openai/v1/chat/completions
 *     key: gsk_...
 *     model: llama-3.1-70b-versatile
 *     timeout: 30
 *     max-tokens: 2048
 *     temperature: 0.7
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ai.api")
public class AiProperties {

    /** Endpoint AI API */
    private String url;

    /** API-ключ для авторизации */
    private String key;

    /** Название используемой модели */
    private String model;

    /** Таймаут HTTP-запроса в секундах */
    private int timeout = 30;

    /** Максимальное число токенов в ответе */
    private int maxTokens = 2048;

    /** Температура генерации (0.0–2.0) */
    private double temperature = 0.7;
}
