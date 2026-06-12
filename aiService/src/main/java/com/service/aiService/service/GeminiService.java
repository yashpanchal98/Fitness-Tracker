package com.service.aiService.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.aiService.config.GeminiConfig;
import com.service.aiService.dto.GeminiRecommendationResponse;
import com.service.aiService.entity.Activity;
import com.service.aiService.entity.Recommendation;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class GeminiService {
    
    @Autowired
    private WebClient geminiWebClient;
    
    @Autowired
    private GeminiConfig geminiConfig;
    
    @Autowired
    private ObjectMapper objectMapper;

    public String createPrompt(Activity activity) {
        return """
            You are a fitness expert AI.
            Analyze this fitness activity:
            Activity Type: %s
            Duration: %d minutes
            Calories Burned: %d
            
            Return ONLY a raw JSON object matching this structure:
            {
              "summary": "short fitness advice",
              "improvements": ["point1", "point2"],
              "suggestions": ["point1", "point2"],
              "safety": ["point1", "point2"]
            }
            Do NOT include markdown formatting or backticks.
            """.formatted(
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned()
            );
    }

    public Mono<Recommendation> generateRecommendation(Activity activity) {
        String prompt = createPrompt(activity);
    
        // 1. Start the chain
        return callGeminiRaw(prompt)
                .map(fullResponse -> {
                    // 2. Extract the text field from the deep Google JSON structure
                    try {
                        List<?> candidates = (List<?>) fullResponse.get("candidates");
                        Map<?, ?> firstCandidate = (Map<?, ?>) candidates.get(0);
                        Map<?, ?> content = (Map<?, ?>) firstCandidate.get("content");
                        List<?> parts = (List<?>) content.get("parts");
                        Map<?, ?> firstPart = (Map<?, ?>) parts.get(0);
                        return (String) firstPart.get("text");
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to navigate Gemini JSON", e);
                    }
                })
                .map(this::cleanJson) // 3. Clean the backticks
                .map(json -> {
                    // 4. Map the clean JSON string to your Entity
                    try {
                        GeminiRecommendationResponse dto = objectMapper.readValue(json, GeminiRecommendationResponse.class);
                        
                        // Convert DTO to Recommendation Entity
                        Recommendation rec = new Recommendation();
                        rec.setActivityId(activity.getId());
                        rec.setUserId(activity.getUserId());
                        rec.setActivityType(activity.getType());
                        rec.setImprovements(dto.getImprovements());
                        rec.setSuggestions(dto.getSuggestions());
                        rec.setSafety(dto.getSafety());
                        rec.setCreatedAt(LocalDateTime.now());
                        rec.setRecommendation(dto.getRecommendation());
                        return rec;
                        
                    } catch (Exception e) {
                        log.error("Mapping failed for JSON: {}", json);
                        throw new RuntimeException("JSON Parsing Error", e);
                    }
                });
    }

    private Mono<Map> callGeminiRaw(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("parts", List.of(Map.of("text", prompt)))
                )
        );
    
        return geminiWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("key", geminiConfig.getApiKey())
                        .build())
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class);
    }

    private String cleanJson(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("(?s)```(?:json)?", "").trim();
    }
}