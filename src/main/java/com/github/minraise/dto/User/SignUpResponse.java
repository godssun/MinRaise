package com.github.minraise.dto.User;

import com.github.minraise.entity.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignUpResponse {
	private Long userId;
	private String username;
	private String email;

	// 스태틱 팩토리 메서드 추가
	public static SignUpResponse fromUser(User user) {
		return SignUpResponse.builder()
				.userId(user.getUser_id().longValue()) // Long 타입으로 변환
				.username(user.getUsername())
				.email(user.getEmail())
				.build();
	}
}
