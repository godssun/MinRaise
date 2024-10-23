package com.github.minraise.service;

import com.github.minraise.dto.game.GameRequest;
import com.github.minraise.dto.game.GameResponse;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.GameCounter;
import com.github.minraise.repository.GameCounterRepository;
import com.github.minraise.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
	private final GameRepository gameRepository;
	private final GameCounterRepository gameCounterRepository;


	public GameResponse createGame(GameRequest gameRequest) {
		// GameRequest를 Game 엔티티로 변환하여 저장
		Game savedGame = gameRepository.save(GameRequest.toEntity(gameRequest));
		// 게임 카운터 초기화
		GameCounter gameCounter = GameCounter.initialize(savedGame.getGameId());
		gameCounterRepository.save(gameCounter);

		// 저장된 Game 엔티티를 GameResponse로 변환하여 반환
		return GameResponse.fromGame(savedGame);
	}

	public GameResponse getGameById(Long gameId) {
		Game game = gameRepository.findById(gameId)
				.orElseThrow(() -> new RuntimeException("Game not found"));

		return GameResponse.fromGame(game);
	}
}
