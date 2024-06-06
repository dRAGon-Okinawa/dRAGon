package ai.dragon.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ai.dragon.util.VersionUtil;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi app() {
        return GroupedOpenApi.builder()
                .group("app")
                .pathsToMatch("/api/app/**")
                .build();
    }

    @Bean
    public GroupedOpenApi backend() {
        return GroupedOpenApi.builder()
                .group("backend")
                .pathsToMatch("/api/backend/**")
                .build();
    }

    @Bean
    public GroupedOpenApi rag() {
        return GroupedOpenApi.builder()
                .group("rag")
                .pathsToMatch("/api/rag/**")
                .build();
    }

    @Bean
    public GroupedOpenApi raag() {
        return GroupedOpenApi.builder()
                .group("raag")
                .pathsToMatch("/api/raag/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("dRAGon API")
                        .description("dRAGon API Reference for Developers")
                        .version(VersionUtil.getVersion())
                        .license(new License().name("MIT License")
                                .url("https://github.com/dRAGon-Okinawa/dRAGon/blob/main/LICENSE")))
                .externalDocs(new ExternalDocumentation()
                        .description("dRAGon Documentation")
                        .url("https://dragon.okinawa"));
    }
}
