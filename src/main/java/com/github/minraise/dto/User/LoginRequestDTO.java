package com.github.minraise.dto.User;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDTO {
	private String username;
	private String password;
}
