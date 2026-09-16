package de.phil.fitness.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Describes the API for the generated OpenAPI document and Swagger UI.
 *
 * Every operation requires the bearer access token by default. Public endpoints opt out
 * with an empty {@code @SecurityRequirements} on their controller. Which paths are documented and
 * whether the docs are served at all is configured under {@code springdoc.*} in application.yaml.
 */
@Configuration
public class OpenApiConfig {

    public static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Fitness App Backend API")
                        .version("0.0.1")
                        .description("Errors share the ErrorResponse shape, see docs/api/ErrorHandling.md."))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Access token from POST /backend/auth/login. Refresh tokens are rejected.")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME));
    }
}
