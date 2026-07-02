package com.coworkflex.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "CoWork-Flex API",
        version     = "1.0.0",
        description = "API REST de réservation d'espaces de coworking",
        contact     = @Contact(
            name  = "CoWork-Flex Team",
            email = "dev@coworkflex.com"
        )
    ),
    servers = {
        @Server(url = "http://localhost:8080",
                description = "Serveur de développement"),
        @Server(url = "https://api.coworkflex.com",
                description = "Serveur de production")
    }
)
@SecurityScheme(
    name         = "bearerAuth",
    type         = SecuritySchemeType.HTTP,
    scheme       = "bearer",
    bearerFormat = "JWT",
    description  = "JWT token obtenu via POST /api/auth/login"
)
public class SwaggerConfig {
    // Configuration déclarative via annotations OpenAPI
}
