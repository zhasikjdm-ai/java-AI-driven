package com.ai.driven.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое при таймауте запроса к AI API.
 * Возвращает HTTP 504 Gateway Timeout клиенту.
 */
@ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
public class AiTimeoutException extends RuntimeException {

    public AiTimeoutException(String message) {
        super(message);
    }

    public AiTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
