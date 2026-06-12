package com.fitness.activityService.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fitness.activityService.entity.ActivityType;


public class ActivityRequest {

    private String userId;
    private ActivityType type;
    private Integer duration;
    private Integer caloriesBurned;
    private LocalDateTime startTime;
    private Map<String, Object> additionalMetrics;

    public ActivityRequest() {}

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public ActivityType getType() { return type; }
    public void setType(ActivityType type) { this.type = type; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getCaloriesBurned() { return caloriesBurned; }
    public void setCaloriesBurned(Integer caloriesBurned) { this.caloriesBurned = caloriesBurned; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }
    public void setAdditionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; }
}
