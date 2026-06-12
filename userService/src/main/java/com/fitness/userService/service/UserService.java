package com.fitness.userService.service;

import com.fitness.userService.dto.UserResponse;
import com.fitness.userService.repository.UserRepository;
import com.fitness.userService.dto.UserRequest;
import com.fitness.userService.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse getUserProfile(String userId) {
        System.out.println("User profile for: " + userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    public UserResponse getUserProfileByKeycloakId(String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new RuntimeException("User not found for keycloak id: " + keycloakId);
        }
        return mapToResponse(user);
    }

    public UserResponse registerUser(UserRequest request) {

        // If user already exists by email, return existing record
        if (userRepository.existsByEmail(request.getEmail())) {
            User existingUser = userRepository.findByEmail(request.getEmail());
            
            // Self-healing: if the user was saved during our earlier tests 
            // when keycloakId was accidentally left null, update it now!
            if (existingUser.getKeycloakId() == null) {
                existingUser.setKeycloakId(request.getKeycloakId());
                existingUser = userRepository.save(existingUser);
            }
            
            return mapToResponse(existingUser);
        }

        // Create and save a brand-new user from Keycloak data
        User user = new User();
        user.setKeycloakId(request.getKeycloakId());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        
        // Fix for SQL Error 1048: Column 'password' cannot be null
        // The MySQL schema still enforces NOT NULL, so we provide a safe placeholder
        user.setPassword(request.getPassword() != null ? request.getPassword() : "KEYCLOAK_SSO_USER");

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    public Boolean checkUser(String userId) {
        // The activity-service calls /api/users/{userId}/exists using the local DB UUID,
        // so we check by primary key (existsById), NOT by keycloakId.
        return userRepository.existsById(userId);
    }

    // ── Helper ───────────────────────────────────────────────────────────────
    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setKeycloakId(user.getKeycloakId());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}