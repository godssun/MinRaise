package com.github.minraise.dto.player;

import com.github.minraise.entity.player.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerResponse {
	private Long playerId;
	private Long gameId;
	private String playerName;
	private int playerIndex;

	public static PlayerResponse from(Player player) {
		return PlayerResponse.builder()
				.playerId(player.getPlayerId())
				.gameId(player.getGame().getGameId())
				.playerName(player.getPlayername())
				.playerIndex(player.getPlayerIndex())
				.build();
	}
}
