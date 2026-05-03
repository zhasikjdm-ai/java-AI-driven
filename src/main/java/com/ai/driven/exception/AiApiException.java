package com.ai.driven.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при ошибке взаимодействия с AI API.
 * Возвращает HTTP 502 Bad Gateway клиенту.
 */
@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class AiApiException extends RuntimeException {

    private final int statusCode;

    /**
     * @param message    описание ошибки
     * @param statusCode HTTP-статус от AI API
     */
    public AiApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public AiApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 502;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
