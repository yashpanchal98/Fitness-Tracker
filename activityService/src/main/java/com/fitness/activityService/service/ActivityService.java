package com.fitness.activityService.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fitness.activityService.client.UserClient;
import com.fitness.activityService.dto.ActivityRequest;
import com.fitness.activityService.dto.ActivityResponse;
import com.fitness.activityService.entity.Activity;
import com.fitness.activityService.repository.ActivityRepository;


@Service
public class ActivityService {

    private static final Logger log = Logger.getLogger(ActivityService.class.getName());

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    UserClient userClient;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.queue.name}")
    private String queue;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public ActivityResponse createActivity(ActivityRequest request) {

        boolean userExists = userClient.checkUserExists(request.getUserId());

        if (!userExists) {
            throw new RuntimeException("Validation Failed: User ID " + request.getUserId() + " does not exist.");
        }

        Activity activity = new Activity();
        activity.setUserId(request.getUserId());
        activity.setType(request.getType());
        activity.setDuration(request.getDuration());
        activity.setCaloriesBurned(request.getCaloriesBurned());
        activity.setStartTime(request.getStartTime());
        activity.setAdditionalMatrix(request.getAdditionalMetrics());

        Activity saved = activityRepository.save(activity);

        try {
            rabbitTemplate.convertAndSend(exchange, routingKey, saved);
        } catch (Exception error) {
            log.severe("Failed to publish over RabbitMQ: " + error.getMessage());
        }

        return mapToResponse(saved);
    }


    private ActivityResponse mapToResponse(Activity activity) {

        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setUserId(activity.getUserId());
        activityResponse.setActivityType(activity.getType());
        activityResponse.setDuration(activity.getDuration());
        activityResponse.setCaloriesBurned(activity.getCaloriesBurned());
        activityResponse.setStartTime(activity.getStartTime());
        activityResponse.setAdditionalMetrics(activity.getAdditionalMatrix());
        activityResponse.setCreatedAt(activity.getCreatedAt());
        activityResponse.setUpdatedAt(activity.getUpdatedAt());
        return activityResponse;
    }

    public ActivityResponse getActivityById(String activityId) {

        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));
        return mapToResponse(activity);
    }

    public List<ActivityResponse> getActivitiesByUser(String userId) {

        List<Activity> activities = activityRepository.findByUserId(userId);

        return activities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}