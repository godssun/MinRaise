package com.github.minraise.service;


import com.github.minraise.dto.bet.BetRequest;
import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.entity.bet.Bet;
import com.github.minraise.entity.bet.BetType;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.GameCounter;
import com.github.minraise.entity.player.Player;
import com.github.minraise.exceptions.BetsNotFoundException;
import com.github.minraise.exceptions.GameNotFoundException;
import com.github.minraise.exceptions.PlayerNotFoundException;
import com.github.minraise.repository.BetRepository;
import com.github.minraise.repository.GameCounterRepository;
import com.github.minraise.repository.GameRepository;
import com.github.minraise.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;



@Service
@RequiredArgsConstructor
public class BetService {
	private final BetRepository betRepository;
	private final PlayerRepository playerRepository;
	private final GameRepository gameRepository;
	private final GameCounterRepository gameCounterRepository;

	@Transactional
	public BetResponse placeBet(BetRequest betRequest) {
		// 데이터 확인 및 초기화
		Player player = findPlayer(betRequest.getGameId(), betRequest.getPlayerIndex());
		Game game = findGame(betRequest.getGameId());
		String position = player.getPosition();
		GameCounter gameCounter = getGameCounter(game.getGameId());
		int currentBetIndex = gameCounter.getBetCounter() + 1;

		BigDecimal raiseAmount;
		BigDecimal previousRaiseAmount;
		BigDecimal requiredBetAmount;


		if (currentBetIndex == 1) {
			raiseAmount = betRequest.getBetAmount().subtract(game.getBigBlind());
			requiredBetAmount = game.getBigBlind().multiply(BigDecimal.valueOf(2));

			if (!validateFirstBet(raiseAmount, game.getBigBlind())) {
				// 잘못된 베팅일 때, requiredBetAmount를 포함해 응답 반환
				return BetResponse.fromInvalid(requiredBetAmount);
			}
		} else if (currentBetIndex == 2) {
			raiseAmount = betRequest.getBetAmount().subtract(game.getCurrentBetAmount());
			previousRaiseAmount = game.getCurrentBetAmount().subtract(game.getBigBlind());
			requiredBetAmount = previousRaiseAmount.add(game.getCurrentBetAmount());

			if (!validateSubsequentBet(raiseAmount, previousRaiseAmount)) {
				return BetResponse.fromInvalid(requiredBetAmount);
			}
		} else {
			BigDecimal lastBetAmount = game.getCurrentBetAmount();
			previousRaiseAmount = lastBetAmount.subtract(getSecondLastBetAmount(game.getGameId()));
			raiseAmount = betRequest.getBetAmount().subtract(lastBetAmount);
			requiredBetAmount = previousRaiseAmount.add(lastBetAmount);

			if (!validateSubsequentBet(raiseAmount, previousRaiseAmount)) {
				return BetResponse.fromInvalid(requiredBetAmount);
			}
		}
		// 유효한 베팅인 경우에만 betCounter와 currentBetAmount를 업데이트
		Bet bet = Bet.builder()
				.game(game)
				.player(player)
				.betAmount(betRequest.getBetAmount())
				.raiseAmount(raiseAmount)
				.requiredBentAmount(requiredBetAmount)
				.position(position)
				.betType(BetType.RAISE.name())
				.isValid(true)
				.betIndex(currentBetIndex)
				.build();

		game.setCurrentBetAmount(betRequest.getBetAmount());
		gameRepository.save(game);

		gameCounter.setBetCounter(currentBetIndex);  // 유효한 베팅일 때만 업데이트
		gameCounterRepository.save(gameCounter);

		// 베팅 저장
		Bet savedBet = betRepository.save(bet);

		// BetResponse로 변환 후 반환
		return BetResponse.from(savedBet,requiredBetAmount);
	}


	// 특정 게임의 모든 베팅 내역 가져오기
	public List<BetResponse> getBetsByGameId(Long gameId) {
		List<Bet> bets = betRepository.findByGame_GameId(gameId);
		return bets.stream()
				.map(bet -> BetResponse.from(bet, BigDecimal.ZERO))  // 기본값으로 BigDecimal.ZERO 전달
				.toList();
	}

	// 특정 게임에서 특정 플레이어 인덱스의 모든 베팅 가져오기
	public List<BetResponse> getBetsByGamePlayerIndex(Long gameId, int playerIndex) {
		List<Bet> bets = betRepository.findByGame_GameIdAndPlayer_PlayerIndex(gameId, playerIndex);
		if (bets.isEmpty()) {
			throw new BetsNotFoundException("No bets found for this player index");
		}
		return bets.stream()
				.map(bet -> BetResponse.from(bet, BigDecimal.ZERO))  // 기본값으로 BigDecimal.ZERO 전달
				.toList();
	}

	// 폴드 메소드
	@Transactional
	public BetResponse fold(Long gameId, int playerIndex) {
		Player player = findPlayer(gameId, playerIndex);

		player.setFolded(true);
		playerRepository.save(player);

		Bet foldBet = createBet(player.getGame(), player, BigDecimal.ZERO, player.getPosition(), BetType.FOLD.name());
		return BetResponse.from(betRepository.save(foldBet),BigDecimal.ZERO);
	}

	// 콜 메소드
	@Transactional
	public BetResponse call(Long gameId, int playerIndex) {
		Game game = findGame(gameId);
		Player player = findPlayer(gameId, playerIndex);


		Bet callBet = createBet(game, player, game.getCurrentBetAmount(), player.getPosition(), BetType.CALL.name());
		return BetResponse.from(betRepository.save(callBet),BigDecimal.ZERO);
	}


	private Player findPlayer(Long gameId, int playerIndex) {
		return playerRepository.findByGame_GameIdAndPlayerIndex(gameId, playerIndex)
				.orElseThrow(() -> new PlayerNotFoundException("Player not found"));
	}

	private Game findGame(Long gameId) {
		return gameRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException("Game not found"));
	}

	private GameCounter getGameCounter(Long gameId) {
		return gameCounterRepository.findById(gameId)
				.orElseThrow(() -> new GameNotFoundException("Game counter not found"));
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

	private Bet createBet(Game game, Player player, BigDecimal betAmount, String position, String betType) {
		int betIndex = gameCounterRepository.findById(game.getGameId())
				.orElseThrow(() -> new GameNotFoundException("Game counter not found"))
				.getBetCounter() + 1;

		return Bet.builder()
				.game(game)
				.player(player)
				.betAmount(betAmount)
				.raiseAmount(BigDecimal.ZERO)
				.position(position)
				.isValid(true)
				.betIndex(betIndex)
				.betType(betType)
				.build();
	}
}