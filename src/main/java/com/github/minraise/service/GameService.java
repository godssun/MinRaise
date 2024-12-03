package com.github.minraise.service;

import com.github.minraise.dto.game.GameDeleteResponse;
import com.github.minraise.dto.game.GameRequest;
import com.github.minraise.dto.game.GameResponse;
import com.github.minraise.dto.game.RoundOverResponse;
import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.entity.bet.Bet;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.GameCounter;
import com.github.minraise.entity.game.RoundState;
import com.github.minraise.entity.player.Player;
import com.github.minraise.entity.user.User;
import com.github.minraise.exceptions.GameNotFoundException;
import com.github.minraise.exceptions.UserNotFoundException;
import com.github.minraise.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
		// 기본값 설정 확인
		if (game.getCurrentRound() == null) {
			game.setCurrentRound(RoundState.PREFLOP);
		}

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

	@Transactional
	public GameResponse processRound(Long gameId) {
		if (isRoundOver(gameId)) {
			// 활성 플레이어가 한 명만 남았으면 게임 종료
			if (playerRepository.findByGame_GameIdAndIsFoldedFalse(gameId).size() == 1) {
				return endGame(gameId);
			}
			// 다음 라운드로 진행
			return nextRound(gameId);
		}
		throw new IllegalStateException("Round is not over yet.");
	}

	protected boolean isRoundOver(Long gameId) {
		System.out.println("Checking if round is over for game ID: " + gameId);
		Game game = findGame(gameId);
		System.out.println(game.getCurrentRound());

		List<Player> activePlayers = playerRepository.findByGame_GameIdAndIsFoldedFalse(gameId);
		System.out.println("Active players count: " + activePlayers.size());

		if (activePlayers.size() == 1) {
			System.out.println("Only one active player left. Round over.");
			return true;
		}

		boolean allPlayersActed = activePlayers.stream().allMatch(Player::isHasTakenAction);
		System.out.println("All players acted: " + allPlayersActed);

		BigDecimal currentBetAmount = findGame(gameId).getCurrentBetAmount();
		boolean allBetsEqual = activePlayers.stream()
				.allMatch(player -> getPlayerBetAmount(player.getPlayerId()).compareTo(currentBetAmount) == 0);
		System.out.println("All bets equal: " + allBetsEqual);


		return allPlayersActed && allBetsEqual;
	}

	private BigDecimal getPlayerBetAmount(Long playerId) {
		return betRepository.findTopByPlayer_PlayerIdOrderByBetIndexDesc(playerId)
				.map(Bet::getBetAmount)
				.orElse(BigDecimal.ZERO);
	}

	@Transactional
	public GameResponse nextRound(Long gameId) {
		Game game = findGame(gameId);

		switch (game.getCurrentRound()) {
			case PREFLOP:
				game.setCurrentRound(RoundState.FLOP);
				break;
			case FLOP:
				game.setCurrentRound(RoundState.TURN);
				break;
			case TURN:
				game.setCurrentRound(RoundState.RIVER);
				break;
			case RIVER:
				game.setCurrentRound(RoundState.FINISHED);
				break;
			default:
				throw new IllegalStateException("Invalid game round.");
		}

		// 폴드한 플레이어를 제외하고 플레이어 상태 초기화
		List<Player> activePlayers = playerRepository.findByGame_GameIdAndIsFoldedFalse(gameId);
		for (Player player : activePlayers) {
			player.setHasTakenAction(false);
			playerRepository.save(player);
		}

		gameRepository.save(game);
		return GameResponse.fromGame(game);
	}

	@Transactional
	protected GameResponse endGame(Long gameId) {
		Game game = findGame(gameId);
		game.setCurrentRound(RoundState.FINISHED);
		gameRepository.save(game);

		Player winner = playerRepository.findByGame_GameIdAndIsFoldedFalse(gameId).get(0);
		return GameResponse.fromGame2(game, winner.getPlayerName());
	}

	private Game findGame(Long gameId) {
		return gameRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException("Game not found"));
	}

	@Transactional
	public RoundOverResponse checkRoundStatus(Long gameId) {
		Game game = findGame(gameId);

		boolean isRoundOver = isRoundOver(gameId);
		RoundState nextRound = null;
		if (isRoundOver) {
			nextRound = switch (game.getCurrentRound()) {
				case PREFLOP -> RoundState.FLOP;
				case FLOP -> RoundState.TURN;
				case TURN -> RoundState.RIVER;
				case RIVER -> RoundState.FINISHED;
				default -> throw new IllegalStateException("Invalid round state");
			};
		}

		List<PlayerResponse> players = playerRepository.findByGame_GameId(gameId)
				.stream()
				.map(PlayerResponse::from)
				.toList();

		return RoundOverResponse.builder()
				.isRoundOver(isRoundOver)
				.currentRound(game.getCurrentRound())
				.nextRound(nextRound)
				.players(players)
				.message(isRoundOver ? "Round Over! Preparing next round..." : "Round ongoing")
				.build();
	}

}


