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
public class BetRequest {
	private Long gameId;
	private Integer playerIndex;
	private BigDecimal betAmount;
	private String position;

	public static BetRequest from(Bet bet) {
		return BetRequest.builder()
				.gameId(bet.getGame().getGameId())
				.playerIndex(bet.getPlayer().getPlayerIndex())
				.betAmount(bet.getBetAmount())
				.position(bet.getPosition())
				.build();
	}

}
