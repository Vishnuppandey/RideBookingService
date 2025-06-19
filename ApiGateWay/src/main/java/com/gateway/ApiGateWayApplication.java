package com.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.codec.ServerCodecConfigurer;

import com.gateway.exception.GlobalExceptionHandler;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGateWayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGateWayApplication.class, args);
	}

	@Bean
	public GlobalExceptionHandler globalExceptionHandler(ServerCodecConfigurer serverCodecConfigurer) {
	    return new GlobalExceptionHandler(serverCodecConfigurer);
	}
}
