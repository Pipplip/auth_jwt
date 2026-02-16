package de.phbe.authjwt.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        val securitySchemeName = "bearerAuth"

        return OpenAPI()
            .info(
                Info()
                    .title("Auth JWT API")
                    .version("1.0.0")
                    .description("REST API mit JWT Authentication")
                    .contact(
                        Contact()
                            .name("PHBE")
                            .email("dev@example.com")
                    )
            )
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")
                    )
            )
            // Global aktiv → alle Endpoints brauchen Token
            //.addSecurityItem(SecurityRequirement().addList(securitySchemeName))

        // wenn ein Endpoint ein accesstoken braucht, dann
        // @SecurityRequirement(name = "bearerAuth")
    }
}