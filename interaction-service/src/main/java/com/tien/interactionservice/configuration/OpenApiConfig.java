package com.tien.interactionservice.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("Interaction Service API")
						.version("1.0.0")
						.description("API documentation for Interaction Service - Comments and Likes/Reactions")
						.contact(new Contact()
								.name("Friendify Team")
								.email("support@friendify.com"))
						.license(new License()
								.name("Apache 2.0")
								.url("https://www.apache.org/licenses/LICENSE-2.0.html")))
				.servers(List.of(
						new Server().url("http://localhost:8088/interaction").description("Local Development Server"),
						new Server().url("http://localhost:8080/interaction").description("API Gateway")
				))
				.addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
				.components(new io.swagger.v3.oas.models.Components()
						.addSecuritySchemes("Bearer Authentication", new SecurityScheme()
								.type(SecurityScheme.Type.HTTP)
								.scheme("bearer")
								.bearerFormat("JWT")
								.description("Enter your JWT token")));
	}
}

