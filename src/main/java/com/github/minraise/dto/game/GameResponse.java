package com.github.minraise.dto.game;

import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.RoundState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameResponse {
	private Long gameId;
	private BigDecimal smallBlind;
	private BigDecimal bigBlind;
	private int maxPlayers;
	private BigDecimal currentBetAmount;
	private List<PlayerResponse> players;
	private List<PlayerResponse> activePlayers;
	private List<BetResponse> bets;
	private Long userId;
	private String winnerName;
	private RoundState currentRound;

	// Static factory method
	public static GameResponse fromGame(Game game) {
		List<PlayerResponse> playerResponses = game.getPlayers() != null ?
				game.getPlayers().stream().map(PlayerResponse::from).toList()
				: new ArrayList<>();

		List<PlayerResponse> activePlayerResponses = game.getPlayers() != null ?
				game.getPlayers().stream()
						.filter(player -> !player.isFolded()) // 폴드하지 않은 플레이어만 포함
						.map(PlayerResponse::from)
						.toList()
				: new ArrayList<>();

		List<BetResponse> betResponses = game.getBets() != null ?
				game.getBets().stream()
						.map(bet -> BetResponse.from(bet, BigDecimal.ZERO))  // 기본값으로 BigDecimal.ZERO 전달
						.toList()
				: new ArrayList<>();

		return GameResponse.builder()
				.gameId(game.getGameId())
				.smallBlind(game.getSmallBlind())
				.bigBlind(game.getBigBlind())
				.maxPlayers(game.getMaxPlayers())
				.currentBetAmount(game.getCurrentBetAmount())
				.players(playerResponses)
				.bets(betResponses)
				.userId(game.getUser().getUserId())
				.currentRound(game.getCurrentRound())
				.build();
	}

	public static GameResponse fromGame2(Game game, String winnerName) {
		List<PlayerResponse> playerResponses = game.getPlayers() != null ?
				game.getPlayers().stream().map(PlayerResponse::from).toList()
				: new ArrayList<>();

		List<PlayerResponse> activePlayerResponses = game.getPlayers() != null ?
				game.getPlayers().stream()
						.filter(player -> !player.isFolded()) // 폴드하지 않은 플레이어만 포함
						.map(PlayerResponse::from)
						.toList()
				: new ArrayList<>();

		List<BetResponse> betResponses = game.getBets() != null ?
				game.getBets().stream()
						.map(bet -> BetResponse.from(bet, BigDecimal.ZERO))  // 기본값으로 BigDecimal.ZERO 전달
						.toList()
				: new ArrayList<>();

		return GameResponse.builder()
				.gameId(game.getGameId())
				.smallBlind(game.getSmallBlind())
				.bigBlind(game.getBigBlind())
				.maxPlayers(game.getMaxPlayers())
				.currentBetAmount(game.getCurrentBetAmount())
				.players(playerResponses)
				.bets(betResponses)
				.userId(game.getUser().getUserId())
				.currentRound(game.getCurrentRound())
				.winnerName(winnerName)  // 추가된 winnerName
				.build();
	}

}
