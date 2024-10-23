package com.github.minraise.service.user;

import com.github.minraise.config.JwtTokenProvider;
import com.github.minraise.dto.User.LoginRequest;
import com.github.minraise.dto.User.LoginResponse;
import com.github.minraise.dto.User.SignUpRequest;
import com.github.minraise.dto.User.SignUpResponse;
import com.github.minraise.entity.user.User;
import com.github.minraise.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;



@Service // 이 클래스는 Spring의 서비스 컴포넌트로 등록됩니다.
@AllArgsConstructor // Lombok을 사용하여 모든 필드를 파라미터로 받는 생성자를 자동으로 생성합니다.
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository; // DB와 상호작용하는 레포지토리
	private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화 및 검증을 위한 인코더
	private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성을 위한 프로바이더
	private final CustomUserDetailsService customUserDetailsService; // 사용자 인증 정보를 관리하는 서비스

	@Override
	@Transactional // 트랜잭션 처리를 보장하여 데이터 일관성을 유지합니다.
	public SignUpResponse registerUser(SignUpRequest signUpRequest) {
		// DTO를 엔티티로 변환하는 정적 팩토리 메소드를 사용합니다.
		User user = User.fromSignUpRequest(signUpRequest,passwordEncoder);
		User savedUser = userRepository.save(user);
		// 변환된 엔티티를 DB에 저장합니다.
		log.info("회원 가입 성공: 사용자명 - {}", user.getUsername());
		return SignUpResponse.fromUser(savedUser);
	}

	public LoginResponse loginUser(LoginRequest loginRequest) {
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getUsername());
		if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}
		String accessToken = jwtTokenProvider.createToken(userDetails.getUsername());
		String refreshToken = jwtTokenProvider.createRefreshToken(userDetails.getUsername());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Refresh-Token", "Bearer " + refreshToken);

		log.info("로그인 성공: 사용자명 - {}", userDetails.getUsername());
		return LoginResponse.builder()
				.message("로그인 완료")
				.headers(headers) // 헤더 추가
				.build();

	}
}
