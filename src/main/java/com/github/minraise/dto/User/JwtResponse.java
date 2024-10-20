package com.github.minraise.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class JwtResponse {
	private String token;
	private String type = "Bearer";

	public JwtResponse(String token) {
		this.token = token;
	}


}