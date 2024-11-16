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
	private String position;

	public static PlayerResponse from(Player player) {
		return PlayerResponse.builder()
				.gameId(player.getGame().getGameId())
				.playerId(player.getPlayerId())
				.playerName(player.getPlayerName())
				.playerIndex(player.getPlayerIndex())
				.position(player.getPosition())
				.build();
	}
}
