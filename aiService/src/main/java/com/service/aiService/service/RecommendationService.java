package com.service.aiService.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.service.aiService.dto.GeminiRecommendationResponse;
import com.service.aiService.entity.Activity;
import com.service.aiService.entity.Recommendation;
import com.service.aiService.repository.RecommendationRepository;

@Service
public class RecommendationService {

    @Autowired
    RecommendationRepository recommendationRepository;


    // public Recommendation generateRecommendation(RecommendationRequest request) {

       
    //     // 2. AI Logic
    //     // Ensure this DTO does NOT contain JsonNode types
    //     GeminiRecommendationResponse aiResponse = GeminiService.generateRecommendation(activity);

    //     // 3. Mapping (Lombok @Builder is perfect here)
    //     Recommendation recommendation = Recommendation.builder()
    //             .user(user)
    //             .activity(activity)
    //             .type("AI_GENERATED")
    //             .recommendation(aiResponse.getSummary())
    //             .improvements(aiResponse.getImprovements())
    //             .suggestions(aiResponse.getSuggestions())
    //             .safety(aiResponse.getSafety())
    //             .createdAt(LocalDateTime.now())
    //             .updatedAt(LocalDateTime.now())
    //             .build();

    //     return recommendationRepository.save(recommendation);
    // }
    
    // by user id
    public List<Recommendation> getRecommendationsByUser(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    // activity id
    public Recommendation getRecommendationsByActivity(String activityId) {
        return recommendationRepository.findByActivityId(activityId)
                .orElseThrow(() -> new RuntimeException("Recommendation not found!"));
    }
}
