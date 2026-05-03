package com.ai.driven.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * Конфигурация WebClient для HTTP-запросов к AI API.
 * Настраивает таймауты соединения, чтения и записи.
 */
@Slf4j
@Configuration
public class WebClientConfig {

    /**
     * Создаёт WebClient с настроенными таймаутами и логированием.
     *
     * @param aiProperties настройки AI API из application.yml
     * @return настроенный WebClient
     */
    @Bean
    public WebClient webClient(AiProperties aiProperties) {
        int timeoutSeconds = aiProperties.getTimeout();

        // Настройка Netty HTTP клиента с таймаутами
        HttpClient httpClient = HttpClient.create()
                // Таймаут установки соединения
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeoutSeconds * 1000)
                // Таймаут ответа
                .responseTimeout(Duration.ofSeconds(timeoutSeconds))
                // Хендлеры чтения/записи
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(timeoutSeconds, TimeUnit.SECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                // Фильтр логирования запросов
                .filter(logRequest())
                // Фильтр логирования ответов
                .filter(logResponse())
                .build();
    }

    /**
     * Фильтр логирования исходящих HTTP-запросов.
     */
    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.debug("→ HTTP {} {}", request.method(), request.url());
            return Mono.just(request);
        });
    }

    /**
     * Фильтр логирования входящих HTTP-ответов.
     */
    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.debug("← HTTP Status: {}", response.statusCode());
            return Mono.just(response);
        });
    }
}
