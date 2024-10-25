package com.github.minraise.controller;

import com.github.minraise.dto.player.PlayerRequest;
import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

	private final PlayerService playerService;

	@PostMapping("/add")
	public ResponseEntity<PlayerResponse> addPlayerToGame(@RequestBody PlayerRequest playerRequest) {
		PlayerResponse addedPlayer = playerService.addPlayer(playerRequest);
		return ResponseEntity.ok(addedPlayer);
	}

	// 특정 게임의 모든 플레이어 정보 가져오기
	@GetMapping("/game/{gameId}")
	public ResponseEntity<List<PlayerResponse>> getPlayersByGameId(@PathVariable Long gameId) {
		List<PlayerResponse> players = playerService.getPlayersByGameId(gameId);
		return ResponseEntity.ok(players);
	}
}
