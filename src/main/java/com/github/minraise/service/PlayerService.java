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

import java.util.List;
import java.util.stream.Collectors;

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

	// 특정 게임의 모든 플레이어 정보를 반환
	public List<PlayerResponse> getPlayersByGameId(Long gameId) {
		// 게임이 존재하는지 확인
		Game game = gameRepository.findById(gameId)
				.orElseThrow(() -> new RuntimeException("Game not found"));

		// 게임에 속한 모든 플레이어를 가져와서 PlayerResponse로 변환
		List<Player> players = playerRepository.findByGame_GameId(gameId);
		return players.stream()
				.map(PlayerResponse::from)
				.collect(Collectors.toList());
	}
}