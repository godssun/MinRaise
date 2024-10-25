package com.github.minraise.exceptions;

import java.math.BigDecimal;


public class MinimumRaiseViolationException extends RuntimeException {
	private final BigDecimal requiredBetAmount;

	public MinimumRaiseViolationException(String message) {
		super(message);
		this.requiredBetAmount = BigDecimal.ZERO;  // 기본값을 0으로 설정
	}
	public MinimumRaiseViolationException(String message, BigDecimal requiredBetAmount) {
		super(message);
		this.requiredBetAmount = requiredBetAmount;
	}

	public BigDecimal getRequiredBetAmount() {
		return requiredBetAmount;
	}
}
