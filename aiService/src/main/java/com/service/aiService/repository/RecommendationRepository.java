package com.service.aiService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.service.aiService.entity.Recommendation;

public interface RecommendationRepository extends MongoRepository<Recommendation,String>{

    List<Recommendation> findByUserId(String userId);
    Optional<Recommendation> findByActivityId(String userId);
} 