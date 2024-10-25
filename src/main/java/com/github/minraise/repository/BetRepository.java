package com.github.minraise.repository;

import com.github.minraise.entity.bet.Bet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BetRepository extends JpaRepository<Bet, Long> {
	Optional<Bet> findByGame_GameIdAndBetIndex(Long gameId, int betIndex);

	List<Bet> findTop2ByGame_GameIdOrderByBetIndexDesc(Long gameId);

}
