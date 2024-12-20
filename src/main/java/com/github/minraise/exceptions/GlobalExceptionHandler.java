package com.github.minraise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidJwtAuthenticationException.class)
	public ResponseEntity<Object> handleInvalidJwtAuthenticationException(InvalidJwtAuthenticationException ex) {
		// 여기에서 상태 코드, 메시지, 에러 응답 구조를 정의합니다.
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN, ex.getMessage()) {
		};
		return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
	}
	@ExceptionHandler(MinimumRaiseViolationException.class)
	public ResponseEntity<Object> handleMinimumRaiseViolationException(MinimumRaiseViolationException ex) {
		// MinimumRaiseViolationException 처리
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	@ExceptionHandler(GameNotFoundException.class)
	public ResponseEntity<Object> handleGameNotFoundException(GameNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(PlayerNotFoundException.class)
	public ResponseEntity<Object> handlePlayerNotFoundException(PlayerNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MaxPlayersExceededException.class)
	public ResponseEntity<Object> handleMaxPlayersExceededException(MaxPlayersExceededException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BetsNotFoundException.class)
	public ResponseEntity<Object> handleBetsNotFoundException(BetsNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException ex) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	// 추가적인 예외 처리 로직
}
