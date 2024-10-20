package com.github.minraise.exceptions;

public class InvalidJwtAuthenticationException extends RuntimeException {
	public InvalidJwtAuthenticationException(String message) {
		super(message);
	}
}
