package com.github.minraise.controller;

import com.github.minraise.dto.User.LoginRequestDTO;
import com.github.minraise.dto.User.UserDTO;
import com.github.minraise.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody UserDTO userDto) {
		userService.registerUser(userDto);
		return ResponseEntity.ok("Register successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
		// 로그인 성공 시 JWT 토큰을 발급
		String token = userService.loginUser(loginRequestDTO);

		// HTTP 헤더에 토큰 포함
		return ResponseEntity.ok()
				.header("Authorization", "Bearer " + token)
				.body("로그인 완료");
	}

}
