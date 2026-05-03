# 🤖 AI-Driven Backend

> Сервис генерации ответов на вопросы с использованием AI (LLM API)

Spring Boot backend, который принимает текстовый запрос, отправляет его в **Groq AI API (LLaMA)** и возвращает ответ через REST API.

---

## 📋 Технологии

| Технология         | Версия  | Назначение                          |
|--------------------|---------|-------------------------------------|
| Java               | 17+     | Язык программирования               |
| Spring Boot        | 3.2.5   | Основной фреймворк                  |
| Spring WebFlux     | —       | WebClient для HTTP-запросов к AI    |
| Spring Validation  | —       | Валидация входящих данных           |
| Lombok             | —       | Сокращение boilerplate-кода         |
| Springdoc OpenAPI  | 2.5.0   | Swagger UI документация             |
| Groq AI API        | —       | LLM провайдер (LLaMA 3.1)           |
| Maven              | 3.8+    | Сборка проекта                      |

---

## 🗂️ Структура проекта

```
src/main/java/com/ai/driven/
├── AiDrivenApplication.java       # Точка входа Spring Boot
├── controller/
│   └── AiController.java          # REST-контроллер (POST /api/ai/ask)
├── service/
│   └── AiService.java             # Бизнес-логика обработки запросов
├── client/
│   └── GroqAiClient.java          # HTTP-клиент для Groq AI API
├── dto/
│   ├── AiRequest.java             # DTO входящего запроса
│   ├── AiResponse.java            # DTO ответа клиенту
│   └── ErrorResponse.java         # DTO ошибок
├── model/
│   ├── GroqRequest.java           # Модель запроса к Groq API
│   └── GroqResponse.java          # Модель ответа от Groq API
├── config/
│   ├── AiProperties.java          # Настройки AI из application.yml
│   ├── SwaggerConfig.java         # Конфигурация Swagger UI
│   └── WebClientConfig.java       # Настройка WebClient + таймауты
└── exception/
    ├── AiApiException.java         # Ошибки AI API (502)
    ├── AiTimeoutException.java     # Таймаут (504)
    └── GlobalExceptionHandler.java # Глобальный обработчик ошибок
```

---

## 🚀 Быстрый старт

### 1. Получите API-ключ Groq

1. Зарегистрируйтесь на [console.groq.com](https://console.groq.com)
2. Перейдите в **API Keys** → **Create API Key**
3. Скопируйте ключ (начинается с `gsk_...`)

> **Groq AI бесплатен** для разработки с высокими лимитами.

### 2. Настройте application.yml

Откройте `src/main/resources/application.yml` и замените:

```yaml
ai:
  api:
    key: YOUR_API_KEY_HERE   # ← Вставьте ваш Groq API ключ
```

Или используйте переменную окружения:

```bash
set AI_API_KEY=gsk_ваш_ключ_здесь   # Windows CMD
$env:AI_API_KEY="gsk_ваш_ключ_здесь" # Windows PowerShell
export AI_API_KEY=gsk_ваш_ключ_здесь # Linux / macOS
```

### 3. Запуск через Maven

```bash
mvn spring-boot:run
```

Или с переменной окружения:

```bash
# Windows PowerShell
$env:AI_API_KEY="gsk_xxx"; mvn spring-boot:run

# Linux / macOS
AI_API_KEY=gsk_xxx mvn spring-boot:run
```

### 4. Сборка JAR

```bash
mvn clean package
java -jar target/ai-driven-backend-1.0.0.jar
```

---

## 🌐 Доступные URL

После запуска сервис доступен на порту **8081**:

| URL                                          | Описание                    |
|----------------------------------------------|-----------------------------|
| `http://localhost:8081/swagger-ui.html`      | Swagger UI (документация)   |
| `http://localhost:8081/api-docs`             | OpenAPI JSON спецификация   |
| `http://localhost:8081/api/ai/ask`           | Основной AI endpoint (POST) |
| `http://localhost:8081/api/ai/health`        | Проверка работоспособности  |

---

## 📡 API Endpoints

### `POST /api/ai/ask` — Задать вопрос AI

**Запрос:**
```json
{
  "message": "Что такое искусственный интеллект?"
}
```

**Успешный ответ (200 OK):**
```json
{
  "success": true,
  "response": "Искусственный интеллект (ИИ) — это область компьютерных наук...",
  "model": "llama-3.1-70b-versatile",
  "processingTimeMs": 1240,
  "timestamp": "2025-05-03T15:00:00"
}
```

**Ошибка валидации (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Входящие данные не прошли валидацию",
  "path": "/api/ai/ask",
  "timestamp": "2025-05-03T15:00:00",
  "validationErrors": {
    "message": "Сообщение не может быть пустым"
  }
}
```

**Ошибка AI API (502 Bad Gateway):**
```json
{
  "status": 502,
  "error": "AI API Error",
  "message": "Ошибка клиента AI API [401]: Invalid API key",
  "path": "/api/ai/ask",
  "timestamp": "2025-05-03T15:00:00"
}
```

---

### `GET /api/ai/health` — Проверка состояния

```json
{
  "success": true,
  "response": "AI-Driven Backend работает корректно.",
  "model": "N/A",
  "timestamp": "2025-05-03T15:00:00"
}
```

---

## 🧪 Примеры запросов

### cURL

```bash
# Задать вопрос AI
curl -X POST http://localhost:8081/api/ai/ask \
  -H "Content-Type: application/json" \
  -d '{"message": "Объясни что такое Spring Boot за 3 предложения"}'

# Проверка работоспособности
curl http://localhost:8081/api/ai/health
```

### PowerShell

```powershell
Invoke-RestMethod -Method Post `
  -Uri "http://localhost:8081/api/ai/ask" `
  -ContentType "application/json" `
  -Body '{"message": "Что такое машинное обучение?"}'
```

### Postman

1. Метод: `POST`
2. URL: `http://localhost:8081/api/ai/ask`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "message": "Ваш вопрос здесь"
}
```

---

## ⚙️ Конфигурация

| Параметр             | По умолчанию                                    | Описание                         |
|----------------------|-------------------------------------------------|----------------------------------|
| `ai.api.url`         | `https://api.groq.com/openai/v1/chat/completions` | Endpoint AI API               |
| `ai.api.key`         | `YOUR_API_KEY_HERE`                             | API-ключ Groq                    |
| `ai.api.model`       | `llama-3.1-70b-versatile`                       | Название AI-модели               |
| `ai.api.timeout`     | `30`                                            | Таймаут запроса (секунды)        |
| `ai.api.max-tokens`  | `2048`                                          | Макс. токенов в ответе           |
| `ai.api.temperature` | `0.7`                                           | Температура генерации (0.0–2.0)  |
| `server.port`        | `8081`                                          | Порт сервера                     |

---

## 🛡️ Обработка ошибок

| Код | Ошибка              | Причина                                 |
|-----|---------------------|-----------------------------------------|
| 400 | Validation Failed   | Пустое или слишком длинное сообщение    |
| 502 | AI API Error        | Неверный ключ, недоступен AI API        |
| 504 | AI API Timeout      | AI не ответил в установленное время     |
| 500 | Internal Server Error | Неожиданная внутренняя ошибка         |

---

## 📦 Зависимости (pom.xml)

- `spring-boot-starter-web` — REST API
- `spring-boot-starter-webflux` — WebClient
- `spring-boot-starter-validation` — Валидация
- `lombok` — Генерация кода
- `springdoc-openapi-starter-webmvc-ui:2.5.0` — Swagger UI

---

## 👤 Автор

Разработан как учебный проект AI-Driven Backend на Spring Boot.
