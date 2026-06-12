package com.service.aiService.entity;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.cglib.core.Local;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class Activity{
    
    private String id;
    private String userId;
    private String type;
    private Integer duration;
    private LocalDateTime startTime;
    private Integer caloriesBurned;
    private Map<String,Object> additionalMatrix;
    private LocalDateTime createdAt;    
    private LocalDateTime updatedAt;
}
