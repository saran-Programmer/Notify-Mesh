package com.notifymesh.notificationservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI notificationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NotifyMesh - Notification Service")
                        .description("Accepts notification requests, validates them, persists to PostgreSQL, and publishes to Kafka")
                        .version("v1"));
    }
}
