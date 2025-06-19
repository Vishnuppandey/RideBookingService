package com.gateway.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.JwtException;
import java.util.Collections;
import java.util.List;

@Order(-2)
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ServerCodecConfigurer serverCodecConfigurer;

    public GlobalExceptionHandler(ServerCodecConfigurer serverCodecConfigurer) {
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
        if (ex instanceof JwtException) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        }

        ServerResponse.Context context = new ServerResponse.Context() {
            @Override
            public List<HttpMessageWriter<?>> messageWriters() {
                return (List<HttpMessageWriter<?>>)(List<?>)serverCodecConfigurer.getWriters();
            }

            @Override
            public List<ViewResolver> viewResolvers() {
                return Collections.emptyList();
            }
        };
        
        return ServerResponse
            .status(exchange.getResponse().getStatusCode())
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(
                Collections.singletonMap("error", ex.getMessage())
            ))
            .flatMap(response -> response.writeTo(exchange, context));
    }
}