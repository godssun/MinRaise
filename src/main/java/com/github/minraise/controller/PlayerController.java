package com.github.minraise.controller;

import com.github.minraise.dto.player.PlayerRequest;
import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@Tag(name = "플레이어 관리", description = "게임 내 플레이어 관리 API")
public class PlayerController {

	private final PlayerService playerService;

	@Operation(summary = "게임에 플레이어 추가", description = "특정 게임에 새로운 플레이어를 추가합니다.")
	@ApiResponse(responseCode = "200", description = "플레이어 추가 성공")
	@PostMapping("/add")
	public ResponseEntity<PlayerResponse> addPlayerToGame(@RequestBody PlayerRequest playerRequest) {
		PlayerResponse addedPlayer = playerService.addPlayer(playerRequest);
		return ResponseEntity.ok(addedPlayer);
	}

	@Operation(summary = "특정 게임의 모든 플레이어 조회", description = "특정 게임에 속한 모든 플레이어의 정보를 가져옵니다.")
	@ApiResponse(responseCode = "200", description = "플레이어 목록 조회 성공")
	@GetMapping("/game/{gameId}")
	public ResponseEntity<List<PlayerResponse>> getPlayersByGameId(@PathVariable Long gameId) {
		List<PlayerResponse> players = playerService.getPlayersByGameId(gameId);
		return ResponseEntity.ok(players);
	}
}
