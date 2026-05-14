package com.sopt.sopkathon_web2_server.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("솝커톤 웹 2팀")
						.description("솝커톤 웹 2팀 서버 API 문서")
						.version(""));
	}
}
