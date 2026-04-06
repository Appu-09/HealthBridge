package com.healthbridge.healthbridge.service;

import com.healthbridge.healthbridge.model.enums.Language;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LlmService {

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.api.url}")
    private String apiUrl;

    private final WebClient.Builder webClientBuilder;

    public String explainReport(String rawText, Language language) {

        String languageInstruction = switch (language) {
            case TELUGU -> "Respond entirely in Telugu language.";
            case HINDI -> "Respond entirely in Hindi language.";
            default -> "Respond in English.";
        };

        String prompt = """
                You are a helpful medical assistant for Indian patients.
                
                %s
                
                A patient has shared their medical lab report. Please:
                1. Explain each test result in very simple, easy-to-understand language
                2. Clearly mention which values are NORMAL and which are ABNORMAL
                3. For abnormal values, explain what it might mean in simple terms
                4. Suggest 3 questions the patient should ask their doctor
                5. Keep the tone friendly, calm and reassuring
                
                Do NOT give a diagnosis. Always remind the patient to consult their doctor.
                
                Here is the lab report:
                %s
                """.formatted(languageInstruction, rawText);

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        System.out.println(">>> CALLING GROQ API...");

        try {
            Map response = webClientBuilder.build()
                    .post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            System.out.println(">>> GROQ EXPLANATION RECEIVED");
            return (String) message.get("content");

        } catch (WebClientResponseException e) {
            System.out.println(">>> GROQ API ERROR: " + e.getResponseBodyAsString());
            throw new RuntimeException("LLM API error: " + e.getResponseBodyAsString());
        }
    }

    public String getDietRecommendations(String rawText, Language language) {

        String languageInstruction = switch (language) {
            case TELUGU -> "Respond entirely in Telugu language.";
            case HINDI -> "Respond entirely in Hindi language.";
            default -> "Respond in English.";
        };

        String prompt = """
                You are a nutrition expert familiar with Indian food and dietary habits.
                
                %s
                
                Based on this medical lab report, suggest specific Indian foods and dietary
                recommendations to help improve any deficient or abnormal values.
                
                Important rules:
                1. Only suggest commonly available Indian foods (dal, sabzi, roti, rice etc.)
                2. Be specific - instead of "eat iron rich foods" say "eat rajma, palak, til"
                3. Also mention foods to AVOID if relevant
                4. Keep it practical for a typical Indian household
                5. Format as a simple, easy to follow list
                
                Here is the lab report:
                %s
                """.formatted(languageInstruction, rawText);

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );

        System.out.println(">>> CALLING GROQ FOR DIET RECOMMENDATIONS...");

        try {
            Map response = webClientBuilder.build()
                    .post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            System.out.println(">>> GROQ DIET RECOMMENDATIONS RECEIVED");
            return (String) message.get("content");

        } catch (WebClientResponseException e) {
            System.out.println(">>> GROQ API ERROR: " + e.getResponseBodyAsString());
            throw new RuntimeException("LLM API error: " + e.getResponseBodyAsString());
        }
    }

    public String callLlm(String prompt, Language language) {

        String languageInstruction = switch (language) {
            case TELUGU -> "Respond entirely in Telugu language.";
            case HINDI -> "Respond entirely in Hindi language.";
            default -> "Respond in English.";
        };

        String fullPrompt = languageInstruction + "\n\n" + prompt;

        Map<String, Object> requestBody = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "user", "content", fullPrompt)
                )
        );

        System.out.println(">>> CALLING GROQ API (generic)...");

        try {
            Map response = webClientBuilder.build()
                    .post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) response.get("choices");
            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            System.out.println(">>> GROQ RESPONSE RECEIVED");
            return (String) message.get("content");

        } catch (WebClientResponseException e) {
            System.out.println(">>> GROQ API ERROR: " + e.getResponseBodyAsString());
            throw new RuntimeException("LLM API error: " + e.getResponseBodyAsString());
        }
    }
}