package com.github.minraise.controller;

import com.github.minraise.dto.game.GameDeleteResponse;
import com.github.minraise.dto.game.GameRequest;
import com.github.minraise.dto.game.GameResponse;
import com.github.minraise.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
@Tag(name = "게임 관리", description = "게임 생성, 조회, 삭제와 관련된 API")
public class GameController {
	private final GameService gameService;

	@Operation(summary = "게임 생성", description = "새로운 게임을 생성합니다.")
	@ApiResponse(responseCode = "200", description = "게임 생성 성공")
	@PostMapping("/create")
	public ResponseEntity<GameResponse> createGame(@RequestBody GameRequest gameRequest) {
		GameResponse gameResponse = gameService.createGame(gameRequest);
		return ResponseEntity.ok(gameResponse);
	}

	@Operation(summary = "게임 조회", description = "게임 ID를 통해 특정 게임의 정보를 조회합니다.")
	@ApiResponse(responseCode = "200", description = "게임 조회 성공")
	@GetMapping("/{gameId}")
	public ResponseEntity<GameResponse> getGameById(@PathVariable Long gameId) {
		GameResponse gameResponse = gameService.getGameById(gameId);
		return ResponseEntity.ok(gameResponse);
	}

	@Operation(summary = "게임 삭제", description = "게임 ID를 통해 특정 게임을 삭제합니다.")
	@ApiResponse(responseCode = "200", description = "게임 삭제 성공")
	@DeleteMapping("/{gameId}")
	public ResponseEntity<GameDeleteResponse> deleteGame(@PathVariable Long gameId) {
		GameDeleteResponse deleteResponse = gameService.deleteGame(gameId);
		return ResponseEntity.ok(deleteResponse);
	}
}
