package com.service.aiService.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "recommendations")
public class Recommendation {
    
    @Id
    private String id;
    private String userId;
    private String activityId;
    private String activityType;
    private String recommendation;
    private List<String> improvements;
    private List<String> suggestions;
    private List<String> safety;
    private LocalDateTime createdAt;

    
}
