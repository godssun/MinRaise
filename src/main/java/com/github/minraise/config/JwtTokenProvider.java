package com.github.minraise.config;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

	// HS256 알고리즘을 사용하여 안전한 키를 생성합니다.
	private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	/**
	 * 사용자 이름을 기반으로 JWT 토큰을 생성합니다.
	 * @param username 사용자 이름
	 * @return 생성된 JWT 토큰
	 */
	public String createToken(String username) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.setSubject(username) // 토큰의 주제 설정
				.setIssuedAt(new Date(now)) // 발행 시간 설정
				.setExpiration(new Date(now + 86400000)) // 토큰의 유효 기간을 24시간으로 설정
				.signWith(SECRET_KEY, SignatureAlgorithm.HS256) // 사용할 서명 알고리즘과 키 지정
				.compact();
	}

	/**
	 * 제공된 JWT 토큰이 유효한지 확인합니다.
	 * @param token 검증하려는 JWT 토큰
	 * @return 토큰 유효성 여부
	 * @throws RuntimeException 토큰이 만료되었거나 유효하지 않은 경우 예외를 발생
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder()
					.setSigningKey(SECRET_KEY)
					.build()
					.parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			throw new RuntimeException("Expired or invalid JWT token");
		}
	}

	/**
	 * JWT 토큰에서 사용자 이름을 추출합니다.
	 * @param token JWT 토큰
	 * @return 토큰에서 추출한 사용자 이름
	 */
	public String getUsernameFromToken(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(SECRET_KEY)
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
}
