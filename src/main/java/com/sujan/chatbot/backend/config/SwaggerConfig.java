package com.sujan.chatbot.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    @Bean
    public OpenAPI chatBotOpenAPI() {

        Server server = new Server();
        server.setUrl(backendUrl);
        server.setDescription("Production Server");

        return new OpenAPI()
                .info(new Info()
                        .title("AI Chatbot API")
                        .description(
                                "REST API for the AI Chatbot application. " +
                                        "Built with Spring Boot 4, Java 21, and PostgreSQL. " +
                                        "Supports JWT authentication, Google OAuth2, " +
                                        "chat session management, and Gemini AI messaging."
                        )
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sujan")
                                .email("sujan.officals@gmail.com"))
                        .license(new License()
                                .name("MIT License"))
                )
                .servers(List.of(server));
    }
}