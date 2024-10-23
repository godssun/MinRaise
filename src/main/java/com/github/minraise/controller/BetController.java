package com.github.minraise.controller;


import com.github.minraise.dto.bet.BetRequest;
import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.service.BetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
