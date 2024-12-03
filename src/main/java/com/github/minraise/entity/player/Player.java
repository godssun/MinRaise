package com.github.minraise.entity.player;

import com.github.minraise.dto.player.PlayerRequest;
import com.github.minraise.entity.bet.Bet;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.RoundState;
import com.github.minraise.repository.BetRepository;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "players")
public class Player {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "player_id")
	private Long playerId;

	@ManyToOne
	@JoinColumn(name = "game_id", referencedColumnName = "game_id")
	private Game game;

	@Column(name = "playername")
	private String playerName;

	private int playerIndex;

	@Column(name = "position")
	private String position;

	@Builder.Default
	private boolean isFolded = false;
	private boolean hasTakenAction;

	public boolean hasTakenAction(RoundState currentRound, BetRepository betRepository) {
		List<Bet> bets = betRepository.findByGame_GameIdAndPlayer_PlayerIdAndRoundState(
				this.getGame().getGameId(),
				this.getPlayerId(),
				currentRound
		);
		return !bets.isEmpty(); // 현재 라운드에서 해당 플레이어의 베팅이 존재하면 true
	}

	public static Player from(PlayerRequest request, Game game, int playerIndex, String position) {
		return Player.builder()
				.game(game)
				.playerIndex(playerIndex)
				.playerName(request.getPlayerName())
				.position(position)
				.isFolded(false)
				.build();
	}
}
