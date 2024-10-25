package com.github.minraise.entity.bet;

import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.entity.player.Player;
import com.github.minraise.entity.game.Game;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bets")
public class Bet {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long betId;

	@ManyToOne
	@JoinColumn(name = "game_id", referencedColumnName = "game_id")
	private Game game;

	@ManyToOne
	@JoinColumn(name = "player_id")
	private Player player;

	private BigDecimal betAmount;
	private BigDecimal raiseAmount;
	private String position;
	private boolean isValid;

	private int betIndex;

	// Bet 엔티티를 BetResponse로 변환하는 static factory method
	public static BetResponse from(Bet bet) {
		return BetResponse.builder()
				.betId(bet.getBetId())
				.gameId(bet.getGame().getGameId())
				.playerId(bet.getPlayer().getPlayerId())
				.betAmount(bet.getBetAmount())
				.raiseAmount(bet.getRaiseAmount())
				.betIndex(bet.getBetIndex())
				.position(bet.getPosition())
				.isValid(bet.isValid())
				.build();
	}
}