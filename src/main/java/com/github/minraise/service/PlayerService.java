package com.github.minraise.service;

import com.github.minraise.dto.player.PlayerRequest;
import com.github.minraise.dto.player.PlayerResponse;
import com.github.minraise.entity.bet.Bet;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.GameCounter;
import com.github.minraise.entity.player.Player;
import com.github.minraise.exceptions.GameNotFoundException;
import com.github.minraise.exceptions.MaxPlayersExceededException;
import com.github.minraise.exceptions.PlayerNotFoundException;
import com.github.minraise.repository.BetRepository;
import com.github.minraise.repository.GameCounterRepository;
import com.github.minraise.repository.GameRepository;
import com.github.minraise.repository.PlayerRepository;
import com.github.minraise.util.PositionConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PlayerService {

	private final GameRepository gameRepository;
	private final PlayerRepository playerRepository;
	private final GameCounterRepository gameCounterRepository;
	private final BetRepository betRepository;


	/**
	 * 게임에 플레이어를 추가하는 메서드
	 *
	 * @param playerRequest 플레이어 추가 요청 정보
	 * @return 추가된 플레이어의 정보를 PlayerResponse 형태로 반환
	 */
	public PlayerResponse addPlayer(PlayerRequest playerRequest) {
		// 게임 ID로 게임을 조회
		Game game = findGameById(playerRequest.getGameId());
		// 게임의 현재 플레이어 수가 최대치를 초과하지 않았는지 확인
		validatePlayerLimit(game);

		// 현재 게임의 플레이어 인덱스를 가져옴
		int currentPlayerIndex = getCurrentPlayerIndex(game.getGameId());
		// 플레이어의 포지션(SB, BB, UTG 등) 결정
		String position = determinePosition(currentPlayerIndex);
		// 초기 베팅 금액 결정
		BigDecimal betAmount = determineInitialBetAmount(currentPlayerIndex, game);

		// 플레이어를 저장
		Player savedPlayer = savePlayer(playerRequest, game, currentPlayerIndex, position);
		// 플레이어 인덱스 카운터를 증가
		updatePlayerCounter(game.getGameId(), currentPlayerIndex + 1);

		// 초기 베팅이 필요한 경우 초기 베팅 생성
		if (isInitialBetRequired(betAmount)) {
			createInitialBet(game, savedPlayer, betAmount, position);
		}

		// 저장된 플레이어 정보를 PlayerResponse로 변환하여 반환
		return PlayerResponse.from(savedPlayer);
	}

	/**
	 * 특정 게임의 모든 플레이어 정보를 반환
	 *
	 * @param gameId 게임 ID
	 * @return 게임에 포함된 모든 플레이어 정보를 PlayerResponse 리스트로 반환
	 */
	public List<PlayerResponse> getPlayersByGameId(Long gameId) {
		// 게임이 존재하는지 확인
		Game game = findGameById(gameId);
		// 게임에 속한 모든 플레이어를 조회
		List<Player> players = playerRepository.findByGame_GameId(game.getGameId());
		// 조회된 플레이어를 PlayerResponse로 변환하여 반환
		return players.stream().map(PlayerResponse::from).toList();
	}

	/**
	 * 게임 ID로 게임을 조회하는 헬퍼 메서드
	 *
	 * @param gameId 게임 ID
	 * @return 조회된 게임 엔티티
	 */
	private Game findGameById(Long gameId) {
		return gameRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException("Game not found with ID: " + gameId));
	}

	/**
	 * 게임의 최대 플레이어 수를 초과하지 않았는지 확인
	 *
	 * @param game 게임 엔티티
	 */
	private void validatePlayerLimit(Game game) {
		int currentPlayersCount = playerRepository.findByGame_GameId(game.getGameId()).size();
		if (currentPlayersCount >= game.getMaxPlayers()) {
			throw new MaxPlayersExceededException("Player limit exceeded for game ID: " + game.getGameId());
		}
	}

	/**
	 * 게임 ID를 기반으로 현재 플레이어 인덱스를 가져오는 메서드
	 *
	 * @param gameId 게임 ID
	 * @return 현재 플레이어 인덱스
	 */
	private int getCurrentPlayerIndex(Long gameId) {
		GameCounter gameCounter = gameCounterRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException("Game counter not found for game ID: " + gameId));
		return gameCounter.getPlayerCounter();
	}

	/**
	 * 현재 플레이어 인덱스에 해당하는 포지션을 결정
	 *
	 * @param currentPlayerIndex 현재 플레이어 인덱스
	 * @return 포지션(SB, BB, UTG 등)
	 */
	private String determinePosition(int currentPlayerIndex) {
		if (currentPlayerIndex >= PositionConstants.POSITIONS.size()) {
			throw new PlayerNotFoundException("Invalid player index: " + currentPlayerIndex);
		}
		return PositionConstants.POSITIONS.get(currentPlayerIndex);
	}

	/**
	 * 초기 베팅 금액을 결정
	 *
	 * @param currentPlayerIndex 현재 플레이어 인덱스
	 * @param game               게임 엔티티
	 * @return 초기 베팅 금액
	 */
	private BigDecimal determineInitialBetAmount(int currentPlayerIndex, Game game) {
		if (currentPlayerIndex == 0) {
			return game.getSmallBlind(); // Small Blind
		} else if (currentPlayerIndex == 1) {
			return game.getBigBlind(); // Big Blind
		}
		return BigDecimal.ZERO; // 다른 포지션은 초기 베팅 금액 없음
	}

	/**
	 * 플레이어 엔티티를 저장
	 *
	 * @param playerRequest       플레이어 추가 요청 정보
	 * @param game                게임 엔티티
	 * @param currentPlayerIndex  현재 플레이어 인덱스
	 * @param position            플레이어의 포지션
	 * @return 저장된 플레이어 엔티티
	 */
	private Player savePlayer(PlayerRequest playerRequest, Game game, int currentPlayerIndex, String position) {
		return playerRepository.save(Player.from(playerRequest, game, currentPlayerIndex, position));
	}

	/**
	 * 게임 카운터의 플레이어 인덱스를 증가
	 *
	 * @param gameId          게임 ID
	 * @param newCounterValue 새로운 플레이어 인덱스 값
	 */
	private void updatePlayerCounter(Long gameId, int newCounterValue) {
		GameCounter gameCounter = gameCounterRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException("Game counter not found for game ID: " + gameId));
		gameCounter.setPlayerCounter(newCounterValue);
		gameCounterRepository.save(gameCounter);
	}

	/**
	 * 초기 베팅이 필요한지 확인
	 *
	 * @param betAmount 초기 베팅 금액
	 * @return 초기 베팅 여부
	 */
	private boolean isInitialBetRequired(BigDecimal betAmount) {
		return betAmount.compareTo(BigDecimal.ZERO) > 0;
	}

	/**
	 * 초기 베팅을 생성하여 저장
	 *
	 * @param game      게임 엔티티
	 * @param player    플레이어 엔티티
	 * @param betAmount 베팅 금액
	 * @param position  포지션
	 */
	private void createInitialBet(Game game, Player player, BigDecimal betAmount, String position) {
		Bet initialBet = Bet.builder()
				.game(game)
				.player(player)
				.betAmount(betAmount)
				.raiseAmount(BigDecimal.ZERO)
				.position(position)
				.isValid(true)
				.betIndex(0) // 초기 베팅 인덱스는 0
				.betType("INITIAL") // 초기 베팅을 구분하기 위한 타입
				.roundState(game.getCurrentRound())
				.build();
		betRepository.save(initialBet);
	}
}