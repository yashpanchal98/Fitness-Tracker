package com.service.aiService.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.netflix.discovery.converters.Auto;
import com.service.aiService.entity.Activity;
import com.service.aiService.entity.Recommendation;
import com.service.aiService.repository.RecommendationRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ActivityMessageListener {


    @Autowired
    private GeminiService geminiService;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @RabbitListener(queues = "activity.queue")
    public void handleActivityMessage(Activity activity) {
        log.info("AI Service received new activity: {}", activity.getId());
        
        // ✅ ONLY USE THIS BLOCK:
        geminiService.generateRecommendation(activity)
        .subscribe(
            result -> {
                log.info("AI Result: {}", result);
                // ✅ Just save it directly. No .subscribe() needed here!
                recommendationRepository.save(result); 
                log.info("Saved to DB successfully");
            },
            error -> log.error("Error: {}", error.getMessage())
        );
    }
}
