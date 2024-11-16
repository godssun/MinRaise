package com.github.minraise.entity.player;

import com.github.minraise.dto.player.PlayerRequest;
import com.github.minraise.entity.game.Game;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
