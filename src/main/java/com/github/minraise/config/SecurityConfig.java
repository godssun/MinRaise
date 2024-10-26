package com.github.minraise.config;

import com.github.minraise.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration // 스프링 설정 클래스를 나타냅니다.
@EnableWebSecurity // 이 어노테이션으로 Spring Security 설정을 활성화합니다.
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable())  // CSRF 보호를 비활성화합니다.
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/api/users/register", "/api/users/**", "/api/calculations/raise", "/api/games/**", "/api/bets/**", "/api/players/**").permitAll() // 회원가입 및 로그인 요청은 인증 없이 접근 허용
						.anyRequest().authenticated()) // 나머지 요청은 인증을 필요로 합니다.
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 사용자 정의 JWT 인증 필터를 기본 인증 필터 전에 추가합니다.

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();  // 기본 AuthenticationManager를 빈으로 등록하여 스프링 시큐리티에서 사용할 수 있도록 합니다.
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();  // 비밀번호를 해시하기 위해 BCrypt 알고리즘을 사용하는 PasswordEncoder의 인스턴스를 생성합니다.
	}
}