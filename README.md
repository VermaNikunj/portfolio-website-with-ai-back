# Portfolio Website - AI Backend

A Spring Boot backend that powers the AI chat assistant on Nikunj Verma's portfolio website, using Google Gemini API.

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Google Gemini 2.0 Flash API
- Apache HttpClient 5
- Lombok
- Docker

## API

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/chat` | Send a message to the AI assistant |

**Request body:**
```json
{ "message": "your question here" }
```

**Response:**
```json
{ "reply": "AI response" }
```

## Getting Started

### Prerequisites
- Java 17+
- Maven
- Gemini API key

### Run Locally

1. Set the environment variable:
   ```bash
   export GEMINI_API_KEY=your_api_key
   ```

2. Build and run:
   ```bash
   ./mvnw spring-boot:run
   ```

Server starts on `http://localhost:8080`

### Run with Docker

```bash
docker build -t portfolio-ai-backend .
docker run -e GEMINI_API_KEY=your_api_key -p 8080:8080 portfolio-ai-backend
```

## Project Structure

```
src/main/java/com/portfolio/portfolio_ai_backend/
├── controller/   # ChatController - REST endpoint
├── service/      # GeminiService - Gemini API integration
├── model/        # ChatRequest, ChatResponse
└── config/       # CorsConfig
```
