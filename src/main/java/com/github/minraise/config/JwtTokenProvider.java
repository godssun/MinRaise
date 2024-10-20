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

	private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

//	private final String secretKey = "mySecretKey";  // 실제로는 환경변수로 관리

	public String createToken(String username) {
		long now = System.currentTimeMillis();
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date(now))
				.setExpiration(new Date(now + 86400000)) // 토큰 유효 기간 (예: 24시간)
				.signWith(SECRET_KEY, SignatureAlgorithm.HS256)
				.compact();
	}

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
}
//..//