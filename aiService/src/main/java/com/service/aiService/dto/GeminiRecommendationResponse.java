package com.service.aiService.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GeminiRecommendationResponse {

    private String recommendation;
    private List<String> improvements;
    private List<String> suggestions;
    private List<String> safety;
}