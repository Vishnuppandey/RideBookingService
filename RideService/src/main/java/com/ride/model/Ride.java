package com.ride.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "rides")
@NoArgsConstructor
@AllArgsConstructor
public class Ride {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="user_id",nullable=false)
	private int user_id;

	@Column(name="driver_id")
	private Integer driverId;

	@Column(name="status",nullable=false,length=20)
	private String status;

	@Column(name="pickup_location",nullable=false,columnDefinition = "TEXT")
	private String pickupLocation;

	@Column(name="dropoff_location",nullable=false,columnDefinition = "TEXT")
	private String dropoff_location;
	
	@Column(name="created_at",nullable=false,columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt=LocalDateTime.now();
}
