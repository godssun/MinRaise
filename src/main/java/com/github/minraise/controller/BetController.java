package com.github.minraise.controller;


import com.github.minraise.dto.bet.BetRequest;
import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.service.BetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bets")
public class BetController {
	private final BetService betService;

	@PostMapping("/place")
	public ResponseEntity<BetResponse> placeBet(@RequestBody BetRequest betRequest) {
		BetResponse response = betService.placeBet(betRequest);
		return ResponseEntity.ok(response);
	}
	// 특정 게임의 모든 베팅 내역 가져오기
	@GetMapping("/game/{gameId}")
	public ResponseEntity<List<BetResponse>> getBetsByGameId(@PathVariable Long gameId) {
		List<BetResponse> bets = betService.getBetsByGameId(gameId);
		return ResponseEntity.ok(bets);
	}

	// 특정 게임의 특정 플레이어 인덱스와 베팅 ID로 베팅 가져오기
	// 특정 게임의 특정 플레이어 인덱스의 모든 베팅 가져오기
	@GetMapping("/game/{gameId}/player-index/{playerIndex}")
	public ResponseEntity<List<BetResponse>> getBetsByGamePlayerIndex(
			@PathVariable Long gameId,
			@PathVariable int playerIndex
	) {
		List<BetResponse> bets = betService.getBetsByGamePlayerIndex(gameId, playerIndex);
		return ResponseEntity.ok(bets);
	}
}
