package com.github.minraise.service;

import com.github.minraise.dto.player.PlayerRequest;
import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.GameCounter;
import com.github.minraise.entity.player.Player;
import com.github.minraise.repository.GameCounterRepository;
import com.github.minraise.repository.GameRepository;
import com.github.minraise.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerService {

	private final GameRepository gameRepository;
	private final PlayerRepository playerRepository;
	private final GameCounterRepository gameCounterRepository;

	public PlayerResponse addPlayer(PlayerRequest playerRequest) {
		// 게임을 찾고 플레이어를 추가하는 로직
		Game game = gameRepository.findById(playerRequest.getGameId())
				.orElseThrow(() -> new RuntimeException("Game not found"));


		GameCounter gameCounter = gameCounterRepository.findById(game.getGameId())
				.orElseThrow(() -> new RuntimeException("Game counter not found"));
		int currentPlayerIndex = gameCounter.getPlayerCounter() + 1;  // 현재 플레이어 인덱스 설정
		gameCounter.setPlayerCounter(currentPlayerIndex);
		gameCounterRepository.save(gameCounter);

// Player 엔티티를 생성하고 저장
		Player savedPlayer = playerRepository.save(Player.from(playerRequest, game, currentPlayerIndex));

		// PlayerResponse로 변환하여 반환
		return PlayerResponse.from(savedPlayer);
	}
}