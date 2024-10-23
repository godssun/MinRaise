package com.github.minraise.repository;

import com.github.minraise.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
	Optional<Player> findByGame_GameIdAndPlayerIndex(Long gameId, Integer playerIndex);
}
