package com.healthbridge.healthbridge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI healthBridgeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HealthBridge API")
                        .description("""
                                India-first Family Health Intelligence Platform.
                                
                                Upload blood test reports, get AI explanations in Telugu/Hindi/English,
                                track family health trends, and receive proactive alerts.
                                
                                **How to use:**
                                1. Register using /api/auth/register
                                2. Login using /api/auth/login — copy the token
                                3. Click 'Authorize' button and paste: Bearer your_token_here
                                4. Now you can test all protected APIs
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("HealthBridge")
                                .email("support@healthbridge.in")))
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Paste your JWT token here")));
    }
}