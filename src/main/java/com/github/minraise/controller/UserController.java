package com.github.minraise.controller;

import com.github.minraise.config.JwtTokenProvider;
import com.github.minraise.dto.User.*;
import com.github.minraise.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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

		// JSON 형식의 응답을 만들기 위해 Map 사용
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("message", loginResponse.getMessage()); // 메시지를 JSON에 포함

		return ResponseEntity.ok()
				.headers(loginResponse.getHeaders())
				.body(responseBody); // JSON 형식의 응답 본문
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
		UserResponse userResponse = userService.getUserById(userId);
		return ResponseEntity.ok(userResponse);
	}
	// 유저 정보 수정 (Update)
	@PutMapping("/{userId}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest updateUserRequest) {
		UserResponse updatedUser = userService.updateUser(userId, updateUserRequest);
		return ResponseEntity.ok(updatedUser);
	}
	// 유저 삭제 (Delete)
	@DeleteMapping("/{userId}")
	public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
		userService.deleteUser(userId);

		// 응답 데이터 생성
		Map<String, Object> response = new HashMap<>();
		response.put("userId", userId);
		response.put("message", "삭제완료");

		return ResponseEntity.ok(response);
	}
}

