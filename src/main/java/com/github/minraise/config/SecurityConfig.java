package com.github.minraise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(CsrfConfigurer::disable)  // CSRF 보호 비활성화
				.authorizeRequests(auth -> auth
						.requestMatchers(new AntPathRequestMatcher("/api/users/register", HttpMethod.POST.name())).permitAll()  // 회원가입 엔드포인트에 대한 접근 허용
						.requestMatchers(new AntPathRequestMatcher("/api/users/login", HttpMethod.POST.name())).permitAll()  // 로그인 엔드포인트에 대한 접근 허용
						.anyRequest().authenticated()  // 그 외 요청은 인증 필요
				);

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}