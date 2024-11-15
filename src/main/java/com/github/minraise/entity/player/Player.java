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

	private String playername;

	private int playerIndex; // 순번 필드 추가

	@Builder.Default
	private boolean isFolded = false; // 폴드 여부를 저장하는 필드 추가

	public static Player from(PlayerRequest playerRequest, Game game, int playerIndex) {
		return Player.builder()
				.game(game)
				.playername(playerRequest.getPlayername())
				.playerIndex(playerIndex)
				.build();
	}
}
