package com.bookrental.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${openapi.title}")
    private String title;

    @Value("${openapi.version}")
    private String version;

    @Value("${openapi.description}")
    private String description;

    @Value("${openapi.server.url}")
    private String serverUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(title)
                        .version(version)
                        .description(description))
                .addServersItem(new Server().url(serverUrl));
    }

    @Bean
    public GroupedOpenApi authenticationApi() {
        return GroupedOpenApi.builder()
                .group("Authentication")
                .pathsToMatch("/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("User")
                .pathsToMatch("/v1/users/**", "/v1/users")
                .build();
    }

    @Bean
    public GroupedOpenApi authorApi() {
        return GroupedOpenApi.builder()
                .group("Author")
                .pathsToMatch("/v1/authors**")
                .build();
    }

    @Bean
    public GroupedOpenApi bookApi() {
        return GroupedOpenApi.builder()
                .group("Book")
                .pathsToMatch("/v1/books/**", "/v1/books")
                .build();
    }

    @Bean
    public GroupedOpenApi rentApi() {
        return GroupedOpenApi.builder()
                .group("Rent")
                .pathsToMatch("/v1/rents/**", "/v1/rents")
                .build();
    }

}
