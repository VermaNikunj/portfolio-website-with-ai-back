package com.portfolio.portfolio_ai_backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.portfolio.portfolio_ai_backend.config.SystemPrompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class GeminiService {

    @Value("${gemini.api.keys}")
    private String[] apiKeys;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @jakarta.annotation.PostConstruct
    public void init() {
        log.info("Loaded {} Gemini API key(s)", apiKeys.length);
    }

    public String chat(String userMessage) {
        log.info("Received message: {}", userMessage);
        RestTemplate restTemplate = new RestTemplate();

        // Build Gemini request body with system prompt and user message
        Map<String, Object> requestBody = Map.of(
            "system_instruction", Map.of(
                "parts", List.of(Map.of("text", SystemPrompt.PROMPT))
            ),
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", userMessage)))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        log.info("before for-loop", apiKeys);

        // Try each API key, rotate to next on 429 (quota exceeded)
        for (int i = 0; i < apiKeys.length; i++) {
            try {
                log.info("Trying key {}...", i + 1);
                String response = restTemplate.exchange(
                                        apiUrl + "?key=" + apiKeys[i].trim(), 
                                        HttpMethod.POST, 
                                        entity, 
                                        String.class
                                    ).getBody();
                log.info("after response", response);
                return extractText(response);
            } catch (HttpClientErrorException e) {
                log.warn("catch", e);
                if (e.getStatusCode().value() == 429) log.warn("Key {} exhausted, trying next...", i + 1);
                else break;
            } catch (Exception e) {
                log.error("Gemini API error: {}", e.getMessage());
                break;
            }
        }
        // All keys exhausted or unexpected error
        return "Sorry, I'm unable to respond right now. Please try again later.";
    }

    // Extract reply text from Gemini JSON response
    private String extractText(String response) throws Exception {
        var root = new ObjectMapper().readTree(response);
        log.info("Response: {}", response);
        return root.at("/candidates/0/content/parts/0/text").asText();
    }
}
