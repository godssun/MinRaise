package com.github.minraise.controller;

import com.github.minraise.config.JwtTokenProvider;
import com.github.minraise.dto.User.LoginRequest;
import com.github.minraise.dto.User.LoginResponse;
import com.github.minraise.dto.User.SignUpRequest;
import com.github.minraise.dto.User.SignUpResponse;
import com.github.minraise.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;


	@PostMapping("/register")
	public ResponseEntity<SignUpResponse> registerUser(@RequestBody SignUpRequest signUpRequest) {
		SignUpResponse signUpResponse = userService.registerUser(signUpRequest);
		return ResponseEntity.ok(signUpResponse);
	}

	@PostMapping("/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		LoginResponse loginResponse = userService.loginUser(loginRequest);
		return ResponseEntity.ok()
				.headers(loginResponse.getHeaders())
				.body(loginResponse.getMessage());
	}
}

