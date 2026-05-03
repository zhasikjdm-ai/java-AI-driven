package com.ai.driven.controller;

import com.ai.driven.dto.AiRequest;
import com.ai.driven.dto.AiResponse;
import com.ai.driven.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для AI-запросов.
 *
 * Предоставляет endpoint для отправки вопросов к AI-модели
 * и получения ответов в формате JSON.
 *
 * Базовый путь: /api/ai
 */
@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Controller", description = "Endpoints для взаимодействия с AI-моделью")
public class AiController {

    private final AiService aiService;

    /**
     * POST /api/ai/ask
     *
     * Принимает вопрос пользователя и возвращает ответ AI-модели.
     *
     * @param request DTO с текстом вопроса (валидируется @Valid)
     * @return ResponseEntity с ответом AI или ошибкой
     */
    @Operation(
            summary = "Задать вопрос AI",
            description = "Отправляет текстовый вопрос в AI-модель (Groq / LLaMA) и возвращает ответ"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешный ответ от AI",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AiResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "success": true,
                                      "response": "Искусственный интеллект — это...",
                                      "model": "llama-3.1-70b-versatile",
                                      "processingTimeMs": 1240,
                                      "timestamp": "2025-05-03T15:00:00"
                                    }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации — пустой или слишком длинный запрос",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "502",
                    description = "Ошибка при обращении к AI API",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ),
            @ApiResponse(
                    responseCode = "504",
                    description = "Таймаут ожидания ответа от AI API",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
    })
    @PostMapping(
            value = "/ask",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AiResponse> ask(
            @Valid @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Вопрос пользователя",
                    required = true,
                    content = @Content(
                            examples = @ExampleObject(value = """
                                    {
                                      "message": "Что такое искусственный интеллект?"
                                    }
                                    """)
                    )
            )
            AiRequest request) {

        log.info("→ POST /api/ai/ask — получен запрос");

        AiResponse response = aiService.processRequest(request);

        log.info("← POST /api/ai/ask — ответ отправлен (success={})", response.isSuccess());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/ai/health
     *
     * Простая проверка работоспособности сервиса.
     * Не обращается к AI API.
     */
    @Operation(
            summary = "Проверка состояния сервиса",
            description = "Возвращает статус работоспособности AI-сервиса"
    )
    @ApiResponse(responseCode = "200", description = "Сервис работает")
    @GetMapping("/health")
    public ResponseEntity<AiResponse> health() {
        log.debug("GET /api/ai/health — health check");

        AiResponse healthResponse = AiResponse.builder()
                .success(true)
                .response("AI-Driven Backend работает корректно. Используйте POST /api/ai/ask для запросов.")
                .model("N/A")
                .build();

        return ResponseEntity.ok(healthResponse);
    }
}
