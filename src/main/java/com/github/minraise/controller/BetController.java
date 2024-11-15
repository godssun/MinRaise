package com.github.minraise.controller;


import com.github.minraise.dto.bet.BetRequest;
import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.service.BetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bets")
@Tag(name = "베팅 관리", description = "베팅 생성 및 조회와 관련된 API")
public class BetController {
	private final BetService betService;

	@Operation(summary = "베팅 추가", description = "새로운 베팅을 추가합니다.")
	@ApiResponse(responseCode = "200", description = "베팅 추가 성공")
	@PostMapping("/place")
	public ResponseEntity<BetResponse> placeBet(@RequestBody BetRequest betRequest) {
		BetResponse response = betService.placeBet(betRequest);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/fold")
	@Operation(summary = "폴드", description = "플레이어가 폴드합니다.")
	@ApiResponse(responseCode = "200", description = "폴드 성공")
	public ResponseEntity<BetResponse> fold(@RequestParam Long gameId, @RequestParam int playerIndex) {
		BetResponse foldResponse = betService.fold(gameId, playerIndex);
		return ResponseEntity.ok(foldResponse);
	}

	@PostMapping("/call")
	@Operation(summary = "콜", description = "플레이어가 콜합니다.")
	@ApiResponse(responseCode = "200", description = "콜 성공")
	public ResponseEntity<BetResponse> call(@RequestParam Long gameId, @RequestParam int playerIndex) {
		BetResponse callResponse = betService.call(gameId, playerIndex);
		return ResponseEntity.ok(callResponse);
	}

	@Operation(summary = "특정 게임의 모든 베팅 내역 조회", description = "특정 게임 ID를 기준으로 모든 베팅 내역을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "베팅 내역 조회 성공")
	@GetMapping("/game/{gameId}")
	public ResponseEntity<List<BetResponse>> getBetsByGameId(@PathVariable Long gameId) {
		List<BetResponse> bets = betService.getBetsByGameId(gameId);
		return ResponseEntity.ok(bets);
	}

	@Operation(summary = "특정 게임의 특정 플레이어의 베팅 내역 조회", description = "특정 게임 ID와 플레이어 인덱스를 기준으로 해당 플레이어의 모든 베팅 내역을 조회합니다.")
	@ApiResponse(responseCode = "200", description = "플레이어 베팅 내역 조회 성공")
	@GetMapping("/game/{gameId}/player-index/{playerIndex}")
	public ResponseEntity<List<BetResponse>> getBetsByGamePlayerIndex(
			@PathVariable Long gameId,
			@PathVariable int playerIndex
	) {
		List<BetResponse> bets = betService.getBetsByGamePlayerIndex(gameId, playerIndex);
		return ResponseEntity.ok(bets);
	}


}
