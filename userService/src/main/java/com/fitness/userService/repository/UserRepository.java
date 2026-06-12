package com.fitness.userService.repository;

import com.fitness.userService.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    Boolean existsByEmail(String email);
    Boolean existsByKeycloakId(String keycloakId);

    User findByEmail(String email);
    User findByKeycloakId(String keycloakId);
}
