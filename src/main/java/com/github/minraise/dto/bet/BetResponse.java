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
	private int playerIndex;
	private BigDecimal betAmount;
	private BigDecimal raiseAmount;
	private BigDecimal requiredBetAmount;
	private String position;
	private boolean isValid;
	private int betIndex;
	private String betType;

	public static BetResponse from(Bet bet, BigDecimal requiredBetAmount) {
		return BetResponse.builder()
				.betId(bet.getBetId())
				.gameId(bet.getGame().getGameId())
				.playerId(bet.getPlayer().getPlayerId())
				.playerIndex(bet.getPlayer().getPlayerIndex())
				.betAmount(bet.getBetAmount())
				.raiseAmount(bet.getRaiseAmount())
				.requiredBetAmount(bet.getRequiredBentAmount())
				.isValid(bet.isValid())
				.betIndex(bet.getBetIndex())
				.betType(bet.getBetType())
				.position(bet.getPosition())
				.build();
	}

	public static BetResponse fromInvalid(BigDecimal requiredBetAmount) {
		// 잘못된 베팅의 경우, 필요한 금액만 반환하는 응답 생성
		return BetResponse.builder()
				.requiredBetAmount(requiredBetAmount)
				.isValid(false)
				.build();
	}


}
