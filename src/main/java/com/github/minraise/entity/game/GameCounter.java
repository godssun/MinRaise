package com.github.minraise.entity.game;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "game_counters")
public class GameCounter {

	@Id
	private Long gameId;

	@OneToOne
	@JoinColumn(name = "game_id", referencedColumnName = "game_id", insertable = false, updatable = false)
	private Game game;

	private int playerCounter;
	private int betCounter;

	// static factory method
	public static GameCounter initialize(Long gameId) {
		return GameCounter.builder()
				.gameId(gameId)
				.playerCounter(0)
				.betCounter(0)
				.build();
	}
}
