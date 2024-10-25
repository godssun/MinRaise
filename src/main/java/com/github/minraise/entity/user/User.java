package com.github.minraise.entity.user;

import com.github.minraise.dto.User.SignUpRequest;
import com.github.minraise.entity.game.Game;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer user_id;

	private String username;

	private String password;

	private String email;

	@OneToMany(mappedBy = "user")
	private List<Game> games;

	@CreationTimestamp
	private LocalDateTime createAt;

	public static User fromSignUpRequest(SignUpRequest request, BCryptPasswordEncoder passwordEncoder) {
		User user = new User();
		user.username = request.getUsername();
		user.email = request.getEmail();
		user.password = passwordEncoder.encode(request.getPassword());
		return user;
	}
}
