package com.github.minraise.exceptions;

public class MaxPlayersExceededException extends RuntimeException {
	public MaxPlayersExceededException(String message) {
		super(message);
	}
}
