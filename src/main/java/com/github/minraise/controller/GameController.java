package com.github.minraise.controller;

import com.github.minraise.dto.game.GameRequest;
import com.github.minraise.dto.game.GameResponse;
import com.github.minraise.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/games")
public class GameController {
	private final GameService gameService;

	@PostMapping("/create")
	public ResponseEntity<?> createGame(@RequestBody GameRequest gameRequest) {
		GameResponse gameResponse = gameService.createGame(gameRequest);
		return ResponseEntity.ok(gameResponse);
	}

	@GetMapping("/{gameId}")
	public ResponseEntity<GameResponse> getGameById(@PathVariable Long gameId) {
		GameResponse gameResponse = gameService.getGameById(gameId);
		return ResponseEntity.ok(gameResponse);
	}
}
