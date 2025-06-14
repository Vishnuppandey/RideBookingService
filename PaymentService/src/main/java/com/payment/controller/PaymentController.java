package com.payment.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.model.Payment;
import com.payment.service.PayMentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	@Autowired
	private PayMentService service;

	@PostMapping("/process")
	public ResponseEntity<Payment> savePayment(@RequestBody Map<String, Object> requestBody) {
		Long rideId = Long.valueOf(requestBody.get("rideId").toString());
		Long userId = Long.valueOf(requestBody.get("userId").toString());
		Double amount = Double.valueOf(requestBody.get("amount").toString());
		String method = requestBody.get("method").toString();

		Payment payment = service.processPayment(rideId, userId, amount, method);
		return ResponseEntity.ok(payment);
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<Payment> getPaymentDetails(@PathVariable Long paymentId) {
		Payment payment = service.getPaymentDetails(paymentId);
		return ResponseEntity.ok(payment);
	}
}
