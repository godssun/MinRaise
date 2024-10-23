package com.github.minraise.dto.game;

import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.entity.game.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	private List<BetResponse> bets;

	// Static factory method
	public static GameResponse fromGame(Game game) {
		List<PlayerResponse> playerResponses = game.getPlayers() != null ?
				game.getPlayers().stream().map(PlayerResponse::from).collect(Collectors.toList())
				: new ArrayList<>();

		List<BetResponse> betResponses = game.getBets() != null ?
				game.getBets().stream().map(BetResponse::from).collect(Collectors.toList())
				: new ArrayList<>();

		return GameResponse.builder()
				.gameId(game.getGameId())
				.smallBlind(game.getSmallBlind())
				.bigBlind(game.getBigBlind())
				.maxPlayers(game.getMaxPlayers())
				.currentBetAmount(game.getCurrentBetAmount())
				.players(playerResponses)
				.bets(betResponses)
				.build();
	}
}
