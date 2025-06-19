package com.gateway.filter;

import com.gateway.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtConfig jwtConfig;
    private final DiscoveryClient discoveryClient;

    @Autowired
    public JwtAuthFilter(JwtConfig jwtConfig, DiscoveryClient discoveryClient) {
        this.jwtConfig = jwtConfig;
        this.discoveryClient = discoveryClient;
    }
    private boolean verifyServiceName(String path) {
        if (path.startsWith("/api/v1/users")) {
            List<ServiceInstance> instances = discoveryClient.getInstances("USERSERVICE");
            logger.info("Found {} instances of USERSERVICE", instances.size());
            return !instances.isEmpty();
        }
        return true;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        logger.info("🔒 Processing request for path: {}", path);

        if (isPublicEndpoint(path)) {
            logger.info("⏩ Skipping auth for public endpoint: {}", path);
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest().getHeaders());
        if (token == null) {
            logger.error("❌ No JWT token found");
            return unauthorizedResponse(exchange, "Missing authorization token");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtConfig.secretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            logger.info("✅ Valid JWT for user: {}", claims.getSubject());

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", claims.getSubject())
                    .header("X-User-Roles", String.valueOf(claims.get("roles")))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            logger.error("❌ JWT validation failed: {}", e.getMessage());
            return unauthorizedResponse(exchange, "Invalid token: " + e.getMessage());
        }
    }


    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/api/v1/users/login") ||
                path.startsWith("/api/v1/users/register") ||
                path.startsWith("/gateway-debug") ||
                path.startsWith("/actuator");
    }

    private String extractToken(HttpHeaders headers) {
        String authHeader = headers.getFirst("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer ")) ?
                authHeader.substring(7) : null;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        return buildErrorResponse(exchange, HttpStatus.UNAUTHORIZED, message);
    }

    private Mono<Void> serviceUnavailableResponse(ServerWebExchange exchange, String message) {
        return buildErrorResponse(exchange, HttpStatus.SERVICE_UNAVAILABLE, message);
    }

    private Mono<Void> buildErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String jsonBody = String.format("{\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status.value(), status.getReasonPhrase(), message);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(jsonBody.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}