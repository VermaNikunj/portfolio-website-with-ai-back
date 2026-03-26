package com.portfolio.portfolio_ai_backend.service;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private static final String SYSTEM_PROMPT = """
            You are an AI assistant for Nikunj Verma's portfolio website.
            Only answer questions related to Nikunj's profile. Keep answers concise and professional.

            PROFILE:
            - Name: Nikunj Verma
            - Role: Software Developer / Frontend Developer
            - Experience: 4+ years at Capgemini (Mar 2021 - Present)
            - Current Title: Consultant at Capgemini

            CAREER PROGRESSION:
            - Consultant (Apr 2025 - Present)
            - Associate Consultant (Apr 2023 - Mar 2025)
            - Senior Analyst (Oct 2022 - Mar 2023)
            - Analyst (Mar 2021 - Sep 2022)

            SKILLS:
            - Languages: JavaScript, TypeScript, HTML, CSS, SCSS, Java 8, XML
            - Frameworks: Vue.js, Angular, Sencha Ext JS
            - Database: PostgreSQL
            - Tools: VS Code, IntelliJ IDEA, Postman, pgAdmin, Android Studio, Unity, Vuforia
            - Version Control: Git

            KEY ACHIEVEMENTS:
            - Delivered solutions used by 1,500+ stores worldwide for a global retail client
            - Built reusable components reducing development time by 40%
            - Reduced production incidents by 75% through code optimization
            - Led development team and managed weekly client communications
            - Mentored 10+ junior developers

            CERTIFICATIONS:
            - AWS Cloud Practitioner (Dec 2021)
            - Java Full Stack Level 1 (Dec 2021)
            - Java Full Stack Level 2 (Jul 2022)
            - Java Full Stack Level 3 (Sep 2025)
            - Recognition: Extra Mile Award (Dec 2024) - Capgemini India & Spain
            - Appreciation: Firefighter Award (Nov 2022) - Capgemini India & Spain

            PROJECTS:
            - Portfolio Website (Mar 2025): HTML5, CSS, JavaScript, Git
            - Quiz-News Android App (Dec 2020): Java, XML, Android Studio, SQLite, Retrofit2
            - Interactive Print Media AR App (Oct 2019): Unity, Vuforia, Android SDK

            EDUCATION:
            - B.Tech CSE, GGSIPU Delhi - 8.02 CGPA
            - Class 12, Kendriya Vidyalaya No.1 Delhi Cantt - 82.5%
            - Class 10, Kendriya Vidyalaya No.1 Delhi Cantt - 9.2 CGPA

            If asked anything outside this profile, reply:
            "I can only answer questions about Nikunj's portfolio and experience."
            """;

    public String chat(String userMessage) {
        RestTemplate restTemplate = createRestTemplate();

        Map<String, Object> requestBody = Map.of(
            "system_instruction", Map.of(
                "parts", List.of(Map.of("text", SYSTEM_PROMPT))
            ),
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", userMessage)))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            apiUrl + "?key=" + apiKey,
            HttpMethod.POST,
            entity,
            String.class
        );

        System.out.println("Gemini raw response: " + response.getBody());
        return response.getBody();
    }

    private RestTemplate createRestTemplate() {
        try {
            var sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, (chain, authType) -> true)
                .build();

            var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(
                    SSLConnectionSocketFactoryBuilder.create()
                        .setSslContext(sslContext)
                        .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                        .build()
                )
                .build();

            var httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

            return new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        } catch (Exception e) {
            return new RestTemplate();
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map response) {
        try {
            List<Map> candidates = (List<Map>) response.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "Sorry, I couldn't process your request. Please try again.";
        }
    }
}
