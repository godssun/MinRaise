package com.github.minraise.exceptions;

public class GameNotFoundException extends RuntimeException {
	public GameNotFoundException(String message) {
		super(message);
	}
}
