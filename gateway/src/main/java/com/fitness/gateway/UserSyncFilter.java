package com.fitness.gateway;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Map;
import java.util.logging.Logger;

/**
 * UserSyncFilter runs on every authenticated request.
 *
 * It does two things:
 * 1. SYNC: Extracts Keycloak JWT claims and calls user-service /register
 *    to ensure the user has a local DB profile (idempotent).
 *
 * 2. HEADER INJECTION: Adds X-User-Id and X-User-Email as request headers
 *    so downstream services (user-service, activity-service, ai-service)
 *    can identify the caller without re-validating the JWT themselves.
 */
@Component
public class UserSyncFilter implements WebFilter {

    private static final Logger log = Logger.getLogger(UserSyncFilter.class.getName());

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8083")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return exchange.getPrincipal()
                .doOnNext(p -> log.info("UserSyncFilter: Got Principal class " + p.getClass().getName()))
                .flatMap(principal -> {
                    // Only process authenticated JWT principals
                    if (!(principal instanceof JwtAuthenticationToken jwtAuth)) {
                        log.info("UserSyncFilter: Principal is not JwtAuthenticationToken, skipping sync.");
                        return chain.filter(exchange);
                    }

                    Jwt jwt = jwtAuth.getToken();

                    // ── 1. Extract standard Keycloak claims ──────────────────────────────
                    String keycloakId = jwt.getSubject();               // Keycloak UUID (sub claim)
                    String email      = jwt.getClaimAsString("email");
                    String firstName  = jwt.getClaimAsString("given_name");
                    String lastName   = jwt.getClaimAsString("family_name");

                    if (keycloakId == null || email == null) {
                        log.warning("JWT missing 'sub' or 'email' claim — skipping user sync.");
                        return chain.filter(exchange);
                    }

                    log.info("UserSyncFilter: Syncing user " + email);

                    // ── 2. Inject user identity headers into the forwarded request ───────
                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(r -> r
                                    .header("X-User-Id", keycloakId)
                                    .header("X-User-Email", email)
                            )
                            .build();

                    // ── 3. Build the user-service registration payload ───────────────────
                    Map<String, String> userPayload = Map.of(
                            "keycloakId", keycloakId,
                            "email",      email,
                            "firstName",  firstName != null ? firstName : "",
                            "lastName",   lastName  != null ? lastName  : ""
                    );

                    // ── 4. Sync user to local DB (idempotent) ────────────────────────────
                    return webClient.post()
                            .uri("/api/users/register")
                            .bodyValue(userPayload)
                            .retrieve()
                            .bodyToMono(Map.class)
                            .doOnSuccess(response ->
                                log.info("User sync OK — keycloakId: " + keycloakId))
                            .doOnError(error ->
                                log.warning("User sync failed — " + error.getMessage()))
                            .onErrorResume(error -> Mono.empty()) // fail-safe: don't block the request
                            .then(chain.filter(mutatedExchange));  // forward with injected headers
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("UserSyncFilter: SecurityContext is empty");
                    return chain.filter(exchange);
                }));
    }
}
