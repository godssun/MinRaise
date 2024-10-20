package com.github.minraise.dto.User;

import com.github.minraise.entity.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@Builder
public class UserDTO {
	private String username;
	private String password_hash;
	private String email;

	// Static factory method to convert DTO to Entity
	public static User toEntity(UserDTO userDto, BCryptPasswordEncoder passwordEncoder) {
		User user = new User();
		user.setUsername(userDto.getUsername());
		user.setEmail(userDto.getEmail());
		user.setPassword_hash(passwordEncoder.encode(userDto.getPassword_hash())); // Password encoding here
		return user;
	}
}
