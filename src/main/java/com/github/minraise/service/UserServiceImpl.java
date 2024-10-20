package com.github.minraise.service;

import com.github.minraise.config.JwtTokenProvider;
import com.github.minraise.dto.User.LoginRequestDTO;
import com.github.minraise.dto.User.UserDTO;
import com.github.minraise.entity.User;
import com.github.minraise.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;


	@Override
	@Transactional
	public User registerUser(UserDTO userDto) {
		// Use the static factory method to convert DTO to entity
		User user = UserDTO.toEntity(userDto, passwordEncoder);
		// Save the user entity
		return userRepository.save(user);
	}

	@Override
	public String loginUser(LoginRequestDTO loginRequest) {
		User user = userRepository.findByUsername(loginRequest.getUsername())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword_hash())) {
			throw new BadCredentialsException("Invalid password");
		}

		// JWT 토큰을 생성하는 로직 추가
		String token = jwtTokenProvider.createToken(user.getUsername());
		return token;
	}
}
