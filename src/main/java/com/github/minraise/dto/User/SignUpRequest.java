package com.github.minraise.dto.User;

import com.github.minraise.entity.user.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@Builder
public class SignUpRequest {
	private String username;
	private String password;
	private String email;

}
