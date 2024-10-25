package com.github.minraise.service.user;

import com.github.minraise.entity.user.User;
import com.github.minraise.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // 이 클래스는 Spring의 서비스 컴포넌트로 등록됩니다.
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository; // 유저 정보를 DB에서 가져오기 위한 레포지토리

	// 생성자 의존성 주입을 통해 UserRepository를 주입받습니다.
	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// 사용자명을 기준으로 사용자를 로드하는 메소드 (UserDetails 인터페이스의 메소드 구현)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// DB에서 사용자명을 기준으로 유저를 조회합니다.
		// 유저가 없으면 UsernameNotFoundException을 던집니다.
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

		// UserDetails 객체를 반환합니다.
		// Spring Security에서 제공하는 User 클래스를 사용해 UserDetails 객체를 만듭니다.
		return org.springframework.security.core.userdetails.User.builder()
				.username(user.getUsername()) // 유저명 설정
				.password(user.getPassword()) // 비밀번호 설정 (DB에서 암호화된 비밀번호)
				.roles("USER") // 사용자의 권한 부여 (예시로 USER 역할)
				.build();
	}
}
