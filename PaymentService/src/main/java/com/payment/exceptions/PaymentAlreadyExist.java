package com.payment.exceptions;

public class PaymentAlreadyExist extends RuntimeException {

	public PaymentAlreadyExist(String message) {
		super(message);
	}
}
