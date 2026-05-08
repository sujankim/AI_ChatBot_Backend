package com.sujan.chatbot.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI chatBotOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Chatbot API")
                        .description(
                                "REST API for the AI Chatbot application. " +
                                        "Built with Spring Boot 4, Java 21, and MySQL. " +
                                        "Supports chat session management and AI-powered messaging."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sujan")
                                .email("your-email@example.com"))
                        .license(new License()
                                .name("MIT License"))
                )
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ));
    }
}
