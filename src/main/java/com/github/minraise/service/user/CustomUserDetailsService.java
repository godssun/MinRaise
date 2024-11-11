package com.github.minraise.service.user;

import com.github.minraise.entity.user.CustomUserDetails;
import com.github.minraise.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// username을 통해 유저 정보를 로드하는 기존 메서드
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	}

	// userId를 통해 유저 정보를 로드하는 새로운 메서드
	public UserDetails loadUserById(Long userId) {
		return userRepository.findById(userId)
				.map(CustomUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
	}
}
