package com.portfolio.portfolio_ai_backend.controller;

import com.portfolio.portfolio_ai_backend.model.ChatRequest;
import com.portfolio.portfolio_ai_backend.model.ChatResponse;
import com.portfolio.portfolio_ai_backend.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String reply = geminiService.chat(request.getMessage());
        return new ChatResponse(reply);
    }
}
