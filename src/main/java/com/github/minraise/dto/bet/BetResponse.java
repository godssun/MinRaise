package com.github.minraise.dto.bet;

import com.github.minraise.entity.bet.Bet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BetResponse {
	private Long betId;
	private Long gameId;
	private Long playerId;
	private BigDecimal betAmount;
	private BigDecimal raiseAmount;
	private String position;
	private boolean isValid;
	private int betIndex;

	public static BetResponse from(Bet bet) {
		return BetResponse.builder()
				.betId(bet.getBetId())
				.gameId(bet.getGame().getGameId())
				.playerId(bet.getPlayer().getPlayerId())
				.betAmount(bet.getBetAmount())
				.raiseAmount(bet.getRaiseAmount())
				.position(bet.getPosition())
				.isValid(bet.isValid())
				.betIndex(bet.getBetIndex())
				.build();
	}
}
