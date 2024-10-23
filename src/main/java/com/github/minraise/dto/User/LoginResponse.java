package com.github.minraise.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
	private HttpHeaders headers;
	private String message;


}
