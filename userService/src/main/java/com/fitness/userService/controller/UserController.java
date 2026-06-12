package com.fitness.userService.controller;

import com.fitness.userService.dto.UserRequest;
import com.fitness.userService.dto.UserResponse;

import com.fitness.userService.entity.User;
import com.fitness.userService.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable String userId){
        return ResponseEntity.ok(userService.getUserProfile(userId)); 
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(@RequestHeader("X-User-Id") String keycloakId){
        return ResponseEntity.ok(userService.getUserProfileByKeycloakId(keycloakId));
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody UserRequest request){
        return ResponseEntity.ok(userService.registerUser(request));
    }

    @GetMapping("/{userId}/exists")
    public ResponseEntity<Boolean> checkUserExists(@PathVariable String userId) {
    // We check the DB. If found, returns true.
        boolean exists = userService.checkUser(userId);
        return ResponseEntity.ok(exists);
    }
}
