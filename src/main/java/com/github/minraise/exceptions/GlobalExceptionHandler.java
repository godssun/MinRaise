package com.github.minraise.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
	// 추가적인 예외 처리 로직
}
