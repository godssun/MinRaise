package com.github.minraise.dto.game;

import com.github.minraise.entity.game.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameRequest {
	private BigDecimal smallBlind;
	private BigDecimal bigBlind;
	private int maxPlayers;

	public static Game toEntity(GameRequest request) {
		return Game.builder()
				.smallBlind(request.getSmallBlind())
				.bigBlind(request.getBigBlind())
				.maxPlayers(request.getMaxPlayers())
				.currentBetAmount(request.getBigBlind())
				.build();
	}
}
