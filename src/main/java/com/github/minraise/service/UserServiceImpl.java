package com.github.minraise.service;

import com.github.minraise.config.JwtTokenProvider;
import com.github.minraise.dto.User.LoginRequestDTO;
import com.github.minraise.dto.User.UserDTO;
import com.github.minraise.entity.User;
import com.github.minraise.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service // 이 클래스는 Spring의 서비스 컴포넌트로 등록됩니다.
@AllArgsConstructor // Lombok을 사용하여 모든 필드를 파라미터로 받는 생성자를 자동으로 생성합니다.
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository; // DB와 상호작용하는 레포지토리
	private final BCryptPasswordEncoder passwordEncoder; // 비밀번호 암호화 및 검증을 위한 인코더
	private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 생성을 위한 프로바이더
	private final CustomUserDetailsService customUserDetailsService; // 사용자 인증 정보를 관리하는 서비스

	@Override
	@Transactional // 트랜잭션 처리를 보장하여 데이터 일관성을 유지합니다.
	public User registerUser(UserDTO userDto) {
		// DTO를 엔티티로 변환하는 정적 팩토리 메소드를 사용합니다.
		User user = UserDTO.toEntity(userDto, passwordEncoder);
		// 변환된 엔티티를 DB에 저장합니다.
		return userRepository.save(user);
	}

	@Override
	public String loginUser(LoginRequestDTO loginRequestDTO) {
		// 사용자명을 기준으로 사용자 정보를 로드합니다.
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequestDTO.getUsername());

		// 사용자가 입력한 비밀번호와 저장된 비밀번호를 비교합니다.
		if (passwordEncoder.matches(loginRequestDTO.getPassword(), userDetails.getPassword())) {
			// 비밀번호가 맞다면 JWT 토큰을 생성하여 반환합니다.
			return jwtTokenProvider.createToken(userDetails.getUsername());
		} else {
			// 비밀번호가 틀리면 예외를 던집니다.
			throw new RuntimeException("Invalid credentials");
		}
	}
}
