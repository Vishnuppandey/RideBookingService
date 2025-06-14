package com.payment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.payment.model.Payment;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PaymentAlreadyExist.class)
	public ResponseEntity<String> paymentException(PaymentAlreadyExist ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	@ExceptionHandler(PaymentNotFoundException.class)
	public ResponseEntity<String> paymentNot(PaymentNotFoundException ex){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}
	
}
