package com.payment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.exceptions.PaymentAlreadyExist;
import com.payment.exceptions.PaymentNotFoundException;
import com.payment.model.Payment;
import com.payment.repository.PaymentRepo;

import org.springframework.transaction.annotation.Transactional;

@Service
public class PayMentService {

	@Autowired
	private PaymentRepo repo;

	@Transactional
	public Payment processPayment(Long rideId, Long userId, Double amount, String method) {
		Payment existingPayment = repo.findByRideId(rideId);
		if (existingPayment != null) {
			throw new PaymentAlreadyExist("Payment already exists for this ride.");
		}

		Payment payment = new Payment();
		payment.setRideId(rideId);
		payment.setUserId(userId);
		payment.setAmount(amount);
		payment.setPaymentMethod(method);
		payment.setStatus("COMPLETED");

		return repo.save(payment); // No ResponseEntity, just Payment
	}

	@Transactional(readOnly = true)
	public Payment getPaymentDetails(Long payMentId) {
		return repo.findById(payMentId).orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
	}

}
