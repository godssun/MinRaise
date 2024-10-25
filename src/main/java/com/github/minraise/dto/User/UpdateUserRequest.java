package com.github.minraise.dto.User;

import lombok.Data;

@Data
public class UpdateUserRequest {
	private String username;
	private String email;
}
