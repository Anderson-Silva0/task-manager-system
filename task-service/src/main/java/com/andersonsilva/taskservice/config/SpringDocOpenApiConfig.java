package com.andersonsilva.taskservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocOpenApiConfig {

    @Bean
    public OpenAPI openApi() {

        Info info = new Info()
                .title("Task Service API")
                .description("API para gerenciamento de tarefas")
                .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0"))
                .contact(new Contact().name("Anderson Silva").email("andersonsilvaa0@outlook.com"));

        return new OpenAPI().info(info);
    }

}
