package com.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class HeaderForwardingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(HeaderForwardingFilter.class);

    // Should execute after JwtAuthFilter but before routing
    private static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 10;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.debug("Processing headers for request to {}", exchange.getRequest().getPath());

        // Forward all headers including Authorization
        HttpHeaders headers = exchange.getRequest().getHeaders();
        if (headers.containsKey(HttpHeaders.AUTHORIZATION)) {
            logger.debug("Forwarding Authorization header to downstream services");
        }

        // Forward custom headers from JwtAuthFilter if present
        if (headers.containsKey("X-User-Id")) {
            logger.debug("Forwarding X-User-Id: {}", headers.getFirst("X-User-Id"));
        }

        // No modification needed - Gateway automatically forwards headers
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return ORDER; // Executes after JwtAuthFilter
    }
}