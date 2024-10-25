package com.github.minraise.dto.User;

import com.github.minraise.entity.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
	private Long userId;
	private String username;
	private String email;

	public static UserResponse fromUser(User user) {
		return UserResponse.builder()
				.userId(user.getUser_id().longValue())
				.username(user.getUsername())
				.email(user.getEmail())
				.build();
	}
}
