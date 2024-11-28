package com.github.minraise.service;

import com.github.minraise.dto.game.GameDeleteResponse;
import com.github.minraise.dto.game.GameRequest;
import com.github.minraise.dto.game.GameResponse;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.GameCounter;
import com.github.minraise.entity.user.User;
import com.github.minraise.exceptions.GameNotFoundException;
import com.github.minraise.exceptions.UserNotFoundException;
import com.github.minraise.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
	private final GameRepository gameRepository;
	private final GameCounterRepository gameCounterRepository;
	private final PlayerRepository playerRepository;
	private final BetRepository betRepository;
	private final UserRepository userRepository;


	public GameResponse createGame(GameRequest gameRequest) {
		User user = userRepository.findById(gameRequest.getUserId())
				.orElseThrow(() -> new UserNotFoundException("User not found with ID: " + gameRequest.getUserId()));

		Game game = GameRequest.toEntity(gameRequest);
		game.setUser(user);

		Game savedGame = gameRepository.save(game);
		// 게임 카운터 초기화
		GameCounter gameCounter = GameCounter.initialize(savedGame.getGameId());
		gameCounterRepository.save(gameCounter);

		// 저장된 Game 엔티티를 GameResponse로 변환하여 반환
		return GameResponse.fromGame(savedGame);
	}

	public GameResponse getGameById(Long gameId) {
		Game game = gameRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException("Game not found"));

		return GameResponse.fromGame(game);
	}

	@Transactional
	public GameDeleteResponse deleteGame(Long gameId) {

		betRepository.deleteByGame_GameId(gameId);
		playerRepository.deleteByGame_GameId(gameId);

		// 게임 삭제
		gameRepository.deleteById(gameId);

		// 삭제된 게임 ID와 메시지를 담아 반환
		return GameDeleteResponse.builder()
				.gameId(gameId)
				.message("게임이 삭제되었습니다.")
				.build();
	}


	public List<GameResponse> getGamesByUserId(Long userId) {
		// 사용자 ID를 기반으로 게임 목록을 조회
		List<Game> games = gameRepository.findByUser_UserId(userId);

		// 조회된 게임 목록을 GameResponse로 변환하여 반환
		return games.stream()
				.map(GameResponse::fromGame)
				.collect(Collectors.toList());
	}
}
