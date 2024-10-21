package com.github.minraise.controller;

import com.github.minraise.config.JwtTokenProvider;
import com.github.minraise.dto.User.LoginRequestDTO;
import com.github.minraise.dto.User.UserDTO;
import com.github.minraise.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;


	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody UserDTO userDto) {
		userService.registerUser(userDto);
		return ResponseEntity.ok("Register successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
		// 로그인 성공 시 JWT 토큰을 발급
		String accessToken = userService.loginUser(loginRequestDTO);
		String refreshToken = jwtTokenProvider.createRefreshToken(loginRequestDTO.getUsername());

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Refresh-Token", "Bearer " + refreshToken);

		return ResponseEntity.ok()
				.headers(headers)
				.body("로그인 완료");
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
		if (jwtTokenProvider.validateToken(refreshToken)) {
			String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
			String newAccessToken = jwtTokenProvider.createToken(username);
			String newRefreshToken = jwtTokenProvider.createRefreshToken(username);

			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + newAccessToken);
			headers.add("Refresh-Token", "Bearer " + newRefreshToken);

			return ResponseEntity.ok()
					.headers(headers)
					.body("Access token refreshed successfully");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
		}
	}
}
