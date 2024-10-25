package com.github.minraise.service.user;

import com.github.minraise.config.JwtTokenProvider;
import com.github.minraise.dto.User.*;
import com.github.minraise.entity.user.User;
import com.github.minraise.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService customUserDetailsService;

	public SignUpResponse registerUser(SignUpRequest signUpRequest) {
		// 중복 사용자 확인
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			throw new RuntimeException("이미 존재하는 사용자 이름입니다.");
		}

		// SignUpRequest에서 User 객체 생성 (스태틱 팩토리 메서드 사용)
		User user = User.fromSignUpRequest(signUpRequest, passwordEncoder);
		// User 저장
		User savedUser = userRepository.save(user);

		// SignUpResponse 생성 후 반환
		return SignUpResponse.fromUser(savedUser);
	}

	public LoginResponse loginUser(LoginRequest loginRequest) {
		User user = userRepository.findByUsername(loginRequest.getUsername())
				.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		}


		// 로그인 성공 시 JWT 토큰 생성 및 반환
		String accessToken = jwtTokenProvider.createToken(Long.valueOf(user.getUser_id().toString()));  // user_id를 사용하여 토큰 생성
		String refreshToken = jwtTokenProvider.createRefreshToken(Long.valueOf(user.getUser_id().toString()));

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + accessToken);
		httpHeaders.add("Refresh-Token", "Bearer " + refreshToken);

		return LoginResponse.builder()
				.message("로그인 완료")
				.headers(httpHeaders) // 헤더에 토큰 추가
				.build();
	}

	public UserResponse getUserById(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		return UserResponse.fromUser(user);
	}

	public UserResponse updateUser(Long userId, UpdateUserRequest updateUserRequest) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		user.setUsername(updateUserRequest.getUsername());
		user.setEmail(updateUserRequest.getEmail());

		userRepository.save(user);

		return UserResponse.fromUser(user);
	}

	public void deleteUser(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));
		userRepository.delete(user);
	}

}
