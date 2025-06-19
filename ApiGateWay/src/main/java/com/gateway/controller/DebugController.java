package com.gateway.controller;

import com.gateway.config.JwtConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gateway-debug")
public class DebugController {

    private final RouteLocator routeLocator;
    private final DiscoveryClient discoveryClient;
    private final JwtConfig jwtConfig;

    @Autowired
    public DebugController(RouteLocator routeLocator,
                           DiscoveryClient discoveryClient,
                           JwtConfig jwtConfig) {
        this.routeLocator = routeLocator;
        this.discoveryClient = discoveryClient;
        this.jwtConfig = jwtConfig;
    }

    // 1. Simple Routes Listing
    @GetMapping("/routes")
    public Flux<Map<String, String>> getRoutesSimple() {
        return routeLocator.getRoutes()
                .map(route -> Map.of(
                        "id", route.getId(),
                        "order", String.valueOf(route.getOrder()),
                        "uri", route.getUri().toString(),
                        "predicate", route.getPredicate().toString()
                )); // Properly closed
    }

    // 2. Detailed Routes Analysis
    @GetMapping("/routes/detailed")
    public Mono<Map<String, Object>> getRoutesDetailed() {
        return routeLocator.getRoutes()
                .collectList()
                .map(routes -> {
                    Map<String, Object> result = new LinkedHashMap<>();
                    result.put("count", routes.size());
                    result.put("routes", routes.stream()
                            .map(route -> Map.of(
                                    "id", route.getId(),
                                    "uri", route.getUri().toString(),
                                    "order", route.getOrder(),
                                    "predicate", route.getPredicate().toString(),
                                    "filters", route.getFilters().stream()
                                            .map(filter -> filter.getClass().getSimpleName())
                                            .collect(Collectors.toList())
                            ))
                            .collect(Collectors.toList()));
                    return result;
                });
    }

    // 3. Service Discovery Info
    @GetMapping("/services")
    public Mono<Map<String, List<ServiceInstance>>> getRegisteredServices() {
        return Mono.just(discoveryClient.getServices().stream()
                .collect(Collectors.toMap(
                        service -> service,
                        discoveryClient::getInstances
                ))); // Properly closed
    }

    // 4. JWT Configuration
    @GetMapping("/jwt-config")
    public Mono<Map<String, Object>> getJwtConfig() {
        return Mono.just(Map.of(
                "secretAlgorithm", jwtConfig.secretKey().getAlgorithm(),
                "expiration", jwtConfig.getExpiration()
        )); // Properly closed
    }

    // 5. Request Headers Inspection
    @GetMapping("/headers")
    public Mono<Map<String, List<String>>> getHeaders(
            @RequestHeader Map<String, String> headers) {
        return Mono.just(headers.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> List.of(e.getValue())
                ))); // Properly closed
    }

    // 6. Health Check Summary
    @GetMapping("/health")
    public Mono<Map<String, String>> getHealthSummary() {
        return Mono.just(Map.of(
                "status", "UP",
                "gateway", "Operational",
                "servicesRegistered", String.valueOf(discoveryClient.getServices().size())
        )); // Properly closed
    }
}
