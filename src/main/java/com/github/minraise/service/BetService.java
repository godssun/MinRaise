package com.github.minraise.service;

import com.github.minraise.dto.game.BetDTO;
import com.github.minraise.dto.bet.BetRequest;
import com.github.minraise.dto.bet.BetResponse;
import com.github.minraise.entity.bet.Bet;
import com.github.minraise.entity.game.Game;
import com.github.minraise.entity.game.GameCounter;
import com.github.minraise.entity.player.Player;
import com.github.minraise.repository.BetRepository;
import com.github.minraise.repository.GameCounterRepository;
import com.github.minraise.repository.GameRepository;
import com.github.minraise.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BetService {
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

		// 현재 베팅 금액 계산
		BigDecimal lastBetAmount = game.getCurrentBetAmount();
		BigDecimal raiseAmount = betRequest.getBetAmount().subtract(lastBetAmount);

		// 베팅 생성
		Bet bet = Bet.builder()
				.game(game)
				.player(player)
				.betAmount(betRequest.getBetAmount())
				.raiseAmount(raiseAmount)
				.position(betRequest.getPosition())
				.isValid(validateBet(raiseAmount, game.getBigBlind()))
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

	private boolean validateBet(BigDecimal raiseAmount, BigDecimal bigBlind) {
		// 최소 레이즈 금액 검증 (빅블라인드의 두 배 이상이어야 유효)
		return raiseAmount.compareTo(bigBlind.multiply(new BigDecimal(2))) >= 0;
	}
}