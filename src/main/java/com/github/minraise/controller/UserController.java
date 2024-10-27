package com.github.minraise.controller;

import com.github.minraise.dto.User.*;
import com.github.minraise.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
@Tag(name = "사용자 관리", description = "사용자 계정 관련 API")
public class UserController {

	private final UserService userService;


	@Operation(summary = "사용자 등록", description = "새 사용자를 등록합니다.")
	@ApiResponse(responseCode = "200", description = "사용자 등록 성공")
	@PostMapping("/register")
	public ResponseEntity<SignUpResponse> registerUser(@RequestBody SignUpRequest signUpRequest) {
		SignUpResponse signUpResponse = userService.registerUser(signUpRequest);
		return ResponseEntity.ok(signUpResponse);
	}

	@Operation(summary = "사용자 로그인", description = "사용자를 인증하고 성공 시 JWT 토큰을 반환합니다.")
	@ApiResponse(responseCode = "200", description = "사용자 로그인 성공")
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> loginUser(@RequestBody LoginRequest loginRequest) {
		LoginResponse loginResponse = userService.loginUser(loginRequest);

		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("message", loginResponse.getMessage());

		return ResponseEntity.ok()
				.headers(loginResponse.getHeaders())
				.body(responseBody);
	}

	@Operation(summary = "사용자 정보 조회", description = "사용자 ID를 기준으로 사용자 정보를 가져옵니다.")
	@ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공")
	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
		UserResponse userResponse = userService.getUserById(userId);
		return ResponseEntity.ok(userResponse);
	}

	@Operation(summary = "사용자 정보 수정", description = "기존 사용자 정보를 업데이트합니다.")
	@ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공")
	@PutMapping("/{userId}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequest updateUserRequest) {
		UserResponse updatedUser = userService.updateUser(userId, updateUserRequest);
		return ResponseEntity.ok(updatedUser);
	}

	@Operation(summary = "사용자 삭제", description = "사용자 ID를 기준으로 기존 사용자를 삭제합니다.")
	@ApiResponse(responseCode = "200", description = "사용자 삭제 성공")
	@DeleteMapping("/{userId}")
	public ResponseEntity<UserDeleteResponse> deleteUser(@PathVariable Long userId) {
		UserDeleteResponse userDeleteResponse = userService.deleteUser(userId);
		return ResponseEntity.ok(userDeleteResponse);
	}
}

