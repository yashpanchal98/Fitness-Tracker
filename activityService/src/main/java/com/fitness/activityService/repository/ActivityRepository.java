package com.fitness.activityService.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.fitness.activityService.entity.Activity;

public interface ActivityRepository extends MongoRepository<Activity,String> {
    
    List<Activity> findByUserId(String userId);
}
