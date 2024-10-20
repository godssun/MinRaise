package com.github.minraise.filter;

import com.github.minraise.config.JwtTokenProvider;
import com.github.minraise.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // Spring의 빈으로 등록됩니다.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider tokenProvider; // JWT 토큰을 처리하는 클래스
	private final CustomUserDetailsService customUserDetailsService; // 사용자 인증 정보를 불러오는 서비스

	// 생성자 의존성 주입을 통해 tokenProvider와 customUserDetailsService를 주입받습니다.
	public JwtAuthenticationFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService customUserDetailsService) {
		this.tokenProvider = tokenProvider;
		this.customUserDetailsService = customUserDetailsService;
	}

	// 필터의 핵심 로직을 처리하는 메소드. 요청이 들어올 때마다 실행됩니다.
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		// 요청 헤더에서 JWT 토큰을 추출합니다.
		String token = getJwtFromRequest(request);

		// 추출한 토큰이 유효한지 확인합니다.
		if (token != null && tokenProvider.validateToken(token)) {
			// 토큰에서 사용자명을 추출합니다.
			String username = tokenProvider.getUsernameFromToken(token);

			// 사용자명을 통해 UserDetails를 로드합니다.
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

			// UserDetails를 기반으로 인증 객체를 생성합니다.
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userDetails, null, userDetails.getAuthorities());

			// 인증 객체에 요청 정보를 추가합니다.
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

			// SecurityContext에 인증 객체를 설정하여 이후 보안 검사를 통과할 수 있도록 합니다.
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		// 다음 필터로 요청을 전달합니다.
		chain.doFilter(request, response);
	}

	// HTTP 요청에서 Authorization 헤더에서 JWT 토큰을 추출하는 메소드
	private String getJwtFromRequest(HttpServletRequest request) {
		// Authorization 헤더에서 Bearer 토큰을 추출합니다.
		String bearerToken = request.getHeader("Authorization");
		// Bearer로 시작하는지 확인한 후, 실제 토큰만 반환합니다.
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7); // 'Bearer ' 이후의 토큰 값만 반환
		}
		// 토큰이 없으면 null을 반환합니다.
		return null;
	}
}