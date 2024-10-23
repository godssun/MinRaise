package com.github.minraise.entity.user;

import com.github.minraise.dto.User.SignUpRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer user_id;

	private String username;

	private String password_hash;

	private String email;

	@CreationTimestamp
	private LocalDateTime createAt;

	public static User fromSignUpRequest(SignUpRequest request, BCryptPasswordEncoder passwordEncoder) {
		User user = new User();
		user.username = request.getUsername();
		user.email = request.getEmail();
		user.password_hash = passwordEncoder.encode(request.getPassword());
		return user;
	}
}
