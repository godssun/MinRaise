package com.github.minraise.service;


import com.github.minraise.dto.bet.BetRequest;
import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.entity.bet.Bet;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.GameCounter;
import com.github.minraise.entity.player.Player;
import com.github.minraise.exceptions.MinimumRaiseViolationException;
import com.github.minraise.repository.BetRepository;
import com.github.minraise.repository.GameCounterRepository;
import com.github.minraise.repository.GameRepository;
import com.github.minraise.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BetService {
	private static final Logger log = LoggerFactory.getLogger(BetService.class);
	private final BetRepository betRepository;
	private final PlayerRepository playerRepository;
	private final GameRepository gameRepository;
	private final GameCounterRepository gameCounterRepository;

	@Transactional
	public BetResponse placeBet(BetRequest betRequest) {
		// 플레이어 인덱스로 플레이어 확인
		Player player = playerRepository.findByGame_GameIdAndPlayerIndex(betRequest.getGameId(), betRequest.getPlayerIndex())
				.orElseThrow(() -> new RuntimeException("Player not found"));

		// 게임 확인
		Game game = gameRepository.findById(player.getGame().getGameId())
				.orElseThrow(() -> new RuntimeException("Game not found"));

		GameCounter gameCounter = gameCounterRepository.findById(game.getGameId())
				.orElseThrow(() -> new RuntimeException("Game counter not found"));
		int currentBetIndex = gameCounter.getBetCounter() + 1;  // 현재 베팅 인덱스 설정
		gameCounter.setBetCounter(currentBetIndex);
		gameCounterRepository.save(gameCounter);

		BigDecimal raiseAmount;
		BigDecimal previousRaiseAmount;

		if (currentBetIndex == 1) {
			// 첫 번째 베팅: 빅블라인드의 두 배가 맞는지 확인
			raiseAmount = betRequest.getBetAmount().subtract(game.getBigBlind());
			if (!validateFirstBet(raiseAmount, game.getBigBlind())) {
				throw new MinimumRaiseViolationException("미니멈 레이즈 위반.");
			}
		} else if (currentBetIndex == 2) {
			// 두 번째 베팅: previousRaiseAmount를 리퀘스트의 베팅 금액과 빅블라인드로 비교
			raiseAmount = betRequest.getBetAmount().subtract(game.getCurrentBetAmount());

			// 두 번째 베팅에서는 previousRaiseAmount를 빅블라인드와 비교
			previousRaiseAmount = game.getCurrentBetAmount().subtract(game.getBigBlind());
			if (!validateSubsequentBet(raiseAmount, previousRaiseAmount)) {
				BigDecimal requiredBetAmount = previousRaiseAmount.add(game.getCurrentBetAmount()); // 추가로 얼마를 베팅해야 하는지 계산
				throw new MinimumRaiseViolationException(
						"미니멈 레이즈 위반. 최소한 " + requiredBetAmount + "을 베팅해야 합니다.", requiredBetAmount
				);
			}
		} else {
			// 세 번째 이후 베팅: raiseAmountt와 previousRaiseAmount의 차이를 계산하여 비교
			BigDecimal lastBetAmount = game.getCurrentBetAmount();
			previousRaiseAmount = lastBetAmount.subtract(getSecondLastBetAmount(game.getGameId()));
			BigDecimal a = getSecondLastBetAmount(game.getGameId());
			raiseAmount = betRequest.getBetAmount().subtract(lastBetAmount);

			if (!validateSubsequentBet(raiseAmount, previousRaiseAmount)) {
				BigDecimal requiredBetAmount = previousRaiseAmount.add(lastBetAmount); // 추가로 얼마를 베팅해야 하는지 계산
				throw new MinimumRaiseViolationException(
						"미니멈 레이즈 위반. 최소한 " + requiredBetAmount + "을 베팅해야 합니다.", requiredBetAmount
				);
			}
		}

		// 베팅 생성
		Bet bet = Bet.builder()
				.game(game)
				.player(player)
				.betAmount(betRequest.getBetAmount())
				.raiseAmount(raiseAmount)
				.position(betRequest.getPosition())
				.isValid(true) // 이미 검증이 끝났으므로 유효하다고 설정
				.betIndex(currentBetIndex)
				.build();

		// 게임의 현재 베팅 금액 업데이트
		game.setCurrentBetAmount(betRequest.getBetAmount());
		gameRepository.save(game);

		// 베팅 저장
		Bet savedBet = betRepository.save(bet);

		// BetResponse로 변환 후 반환
		return BetResponse.from(savedBet);
	}

	private boolean validateFirstBet(BigDecimal raiseAmount, BigDecimal bigBlind) {
		// 첫 번째 베팅 검증: 빅블라인드의 두 배 이상이어야 함
		return raiseAmount.compareTo(bigBlind) >= 0;
	}

	private boolean validateSubsequentBet(BigDecimal raiseAmount, BigDecimal previousRaiseAmount) {
		// 이후 베팅 검증: 최소 레이즈 금액은 이전 레이즈 금액과 동일하거나 그 이상이어야 함
		return raiseAmount.compareTo(previousRaiseAmount) >= 0;
	}

	private BigDecimal getSecondLastBetAmount(Long gameId) {
		// 해당 게임에서 마지막 두 개의 베팅을 가져오는 쿼리
		List<Bet> lastTwoBets = betRepository.findTop2ByGame_GameIdOrderByBetIndexDesc(gameId);

		if (lastTwoBets.size() < 2) {
			// 두 번째로 마지막 베팅이 없으면 0 반환
			return BigDecimal.ZERO;
		}

		// 두 번째로 마지막 베팅의 금액 반환
		return lastTwoBets.get(1).getBetAmount();
	}
	private BigDecimal getSecondLastRaiseAmount(Long gameId) {
		// 해당 게임에서 마지막 두 개의 레이즈 양을 가져오는 쿼리
		List<Bet> lastTwoBets = betRepository.findTop2ByGame_GameIdOrderByBetIndexDesc(gameId);

		if (lastTwoBets.size() < 2) {
			// 두 번째로 마지막 베팅이 없으면 0 반환
			return BigDecimal.ZERO;
		}

		// 두 번째로 마지막 베팅의 레이즈 양 반환
		return lastTwoBets.get(1).getRaiseAmount();
	}


	// 특정 게임의 모든 베팅 내역 가져오기
	public List<BetResponse> getBetsByGameId(Long gameId) {
		List<Bet> bets = betRepository.findByGame_GameId(gameId);
		return bets.stream()
				.map(BetResponse::from)
				.collect(Collectors.toList());
	}

	// 특정 게임에서 특정 플레이어 인덱스의 모든 베팅 가져오기
	public List<BetResponse> getBetsByGamePlayerIndex(Long gameId, int playerIndex) {
		List<Bet> bets = betRepository.findByGame_GameIdAndPlayer_PlayerIndex(gameId, playerIndex);
		if (bets.isEmpty()) {
			throw new RuntimeException("No bets found for this player index");
		}
		return bets.stream().map(BetResponse::from).collect(Collectors.toList());
	}
}