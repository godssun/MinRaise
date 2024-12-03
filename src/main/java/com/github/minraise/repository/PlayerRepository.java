package com.github.minraise.repository;

import com.github.minraise.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
	Optional<Player> findByGame_GameIdAndPlayerIndex(Long gameId, Integer playerIndex);

	void deleteByGame_GameId(Long gameId);

	// 특정 게임에 속한 모든 플레이어 가져오기
	List<Player> findByGame_GameId(Long gameId);

	List<Player> findByGame_GameIdAndIsFoldedFalse(Long gameId);
}
