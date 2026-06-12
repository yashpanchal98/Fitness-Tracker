package com.fitness.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class GatewayApplication {

	@GetMapping("/health-check")
	public String checkHealth(){
		return "Gateway is working";
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("user-service", r -> r.path("/api/users/**")
				.uri("lb://USER-SERVICE"))
			.route("activity-service", r -> r.path("/api/activities/**")
				.uri("lb://ACTIVITY-SERVICE"))
			.route("ai-service", r -> r.path("/api/recommendation/**")
				.uri("lb://AI-SERVICE"))
			.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
}
