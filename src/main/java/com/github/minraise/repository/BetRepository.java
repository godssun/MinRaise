package com.github.minraise.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.github.minraise.entity.bet.Bet;
import com.github.minraise.entity.game.RoundState;
import com.github.minraise.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface BetRepository extends JpaRepository<Bet, Long> {
	List<Bet> findTop2ByGame_GameIdOrderByBetIndexDesc(Long gameId);


	void deleteByGame_GameId(Long gameId);


	List<Bet> findByGame_GameId(Long gameId);

	// 특정 게임의 특정 플레이어 인덱스의 모든 베팅 가져오기
	List<Bet> findByGame_GameIdAndPlayer_PlayerIndex(Long gameId, int playerIndex);

	List<Bet> findByGame_GameIdOrderByBetIndexDesc(Long gameId);

	List<Bet> findByGame_GameIdAndPlayer_PlayerIdAndRoundState(Long gameId, Long playerId, RoundState roundState);

	Optional<Bet> findTopByPlayer_PlayerIdOrderByBetIndexDesc(Long playerId);
}
