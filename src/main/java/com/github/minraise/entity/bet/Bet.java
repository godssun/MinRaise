package com.github.minraise.entity.bet;

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
}