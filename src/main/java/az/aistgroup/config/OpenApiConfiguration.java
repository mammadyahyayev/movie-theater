package az.aistgroup.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for OPEN Api.
 */
@Configuration
public class OpenApiConfiguration {

    /**
     * The Bean is used to set base configurations such as project <b>name</b>, <b>version</b>,
     * <b>description</b> and so on.
     *
     * @return {@link OpenAPI}
     */
    @Bean
    public OpenAPI configureOpenApiSecurity() {
        var securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Theater REST API")
                        .version("V1")
                        .description("Movie Theater REST API documentation.")
                )
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
