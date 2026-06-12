package com.fitness.activityService.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitness.activityService.dto.ActivityRequest;
import com.fitness.activityService.dto.ActivityResponse;
import com.fitness.activityService.service.ActivityService;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    // Create Activity
    @PostMapping
    public ResponseEntity<ActivityResponse> trackActivity(@RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityService.createActivity(request));
    }

    // Get activity by ID
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityResponse> getActivityById(@PathVariable String activityId) {

        ActivityResponse activity = activityService.getActivityById(activityId);

        return ResponseEntity.ok(activity);
    }
    
    
    // Get Activity by userID
    @GetMapping("user/{userId}")
    public ResponseEntity<List<ActivityResponse>> getActivitiesByUser(@PathVariable String userId) {
        
        List<ActivityResponse> activities = activityService.getActivitiesByUser(userId);
        return ResponseEntity.ok(activities);
    }
    
}



