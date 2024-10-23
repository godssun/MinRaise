package com.github.minraise.controller;

import com.github.minraise.dto.player.PlayerRequest;
import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
