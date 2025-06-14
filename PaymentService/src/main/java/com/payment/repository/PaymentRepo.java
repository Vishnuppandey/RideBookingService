package com.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.model.Payment;

public interface PaymentRepo extends JpaRepository<Payment, Long> {

	Payment findRideByid(Long rideId);

	Payment findByRideId(Long rideId);
}
