package com.github.minraise.dto.game;

import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.entity.game.RoundState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoundOverResponse {
	private boolean isRoundOver;
	private RoundState currentRound;
	private RoundState nextRound;
	private List<PlayerResponse> players;
	private String message; // Optional: 상황에 대한 메시지
}
