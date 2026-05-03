package com.ai.driven.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Конфигурация Swagger / OpenAPI документации.
 * Swagger UI доступен по адресу: http://localhost:8081/swagger-ui.html
 */
@Configuration
public class SwaggerConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    /**
     * Основной бин OpenAPI с метаданными проекта.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI-Driven Backend API")
                        .version("1.0.0")
                        .description("""
                                # AI-Driven Backend Service
                                
                                Сервис генерации ответов на вопросы с использованием LLM (Large Language Model).
                                
                                ## Возможности
                                - Отправка вопросов к AI-модели (Groq / LLaMA)
                                - Получение структурированных JSON-ответов
                                - Валидация входящих данных
                                - Обработка ошибок
                                
                                ## Использование
                                1. Откройте endpoint `POST /api/ai/ask`
                                2. Введите ваш вопрос в поле `message`
                                3. Получите ответ от AI
                                """)
                        .contact(new Contact()
                                .name("AI-Driven Team")
                                .email("support@ai-driven.dev"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Локальный сервер разработки")
                ));
    }
}
