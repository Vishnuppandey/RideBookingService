package com.payment.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "payments")
@NoArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ride_id", nullable = false)
	private Long rideId;

	@Column(name = "user_Id", nullable = false)
	private Long userId;

	@Column(name = "amount", nullable = false)
	private Double amount;

	@Column(name = "status", nullable = false, length = 20)
	private String status;

	@Column(name = "payment_method", nullable = false)
	private String paymentMethod;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}
