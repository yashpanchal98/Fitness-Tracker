package com.service.aiService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.*;
import org.springframework.web.bind.annotation.RestController;

import com.service.aiService.entity.Recommendation;
import com.service.aiService.service.RecommendationService;

@RestController
@RequestMapping("/api/recommendation")
public class RecommendationController {
    
    @Autowired 
    RecommendationService recommendationService;


    // @PostMapping("/generate")
    // public ResponseEntity<Recommendation> generateRecommendation(@RequestBody Recommendation request) {

    //     Recommendation response =
    //             recommendationService.generateRecommendation(request);

    //     return ResponseEntity.ok(response);
    // }

    // Get user recommendation
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendations(@PathVariable String userId) {

        List<Recommendation> recommendations =
                recommendationService.getRecommendationsByUser(userId);

        return ResponseEntity.ok(recommendations);
    }

    //   Get Activity Recommendation
    @GetMapping("/activity/{activityId}")
    public ResponseEntity<Recommendation> getActivityRecommendations(@PathVariable String activityId) {

        Recommendation recommendations =
                recommendationService.getRecommendationsByActivity(activityId);

        return ResponseEntity.ok(recommendations);
    }

}
