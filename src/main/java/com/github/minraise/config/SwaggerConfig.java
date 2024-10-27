package com.github.minraise.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {

		// 로컬 서버 설정
		Server localServer = new Server();
		localServer.setUrl("http://localhost:8080");  // Swagger를 띄우는 기본 로컬 URL
		localServer.setDescription("Local Server");

		// 운영 서버 설정 (필요시)
		Server prodServer = new Server();
		prodServer.setUrl("운영 URL"); // 실제 운영 URL로 변경
		prodServer.setDescription("Production Server");

		// API 문서 기본 정보 설정
		Info info = new Info()
				.title("Swagger API")       // API 문서 제목
				.version("v1.0.0")          // API 문서 버전
				.description("스웨거 API 문서입니다."); // API 문서 설명

		return new OpenAPI()
				.info(info)
				.servers(List.of(localServer, prodServer));
	}
}
