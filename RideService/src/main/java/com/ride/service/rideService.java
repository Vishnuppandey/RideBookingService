package com.ride.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ride.dto.RideDto;
import com.ride.model.Ride;
import com.ride.repository.RideRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class rideService {

	@Autowired
	private RideRepository repo;

	@Transactional
	public Ride createRide(RideDto rideDto) {
		Ride ride=new Ride();
		ride.setUser_id(1);
		ride.setDriverId(null);
		ride.setStatus("PENDING");
		ride.setPickupLocation(rideDto.getPickupLocation());
		ride.setDropoff_location(rideDto.getDropOfLocation());
		ride.setCreatedAt(LocalDateTime.now());
		return repo.save(ride);
	}

	@Transactional(readOnly = true)
	public Optional<Ride> getride(Long rideId) {
		return repo.findById(rideId);
	}

	@Transactional
	public Ride updateRideStatus(Long rideId, String status) {
		Ride ride = repo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));

		ride.setStatus(status);

		return repo.save(ride);
	}
	
	@Transactional
	public Ride assignDriver(Long rideId,Integer driverId) {
		Ride ride = repo.findById(rideId).orElseThrow(() -> new RuntimeException("Ride not found with ID: " + rideId));
		ride.setDriverId(driverId);
		ride.setStatus("Assigned");
		return repo.save(ride);
	}
	
	@Transactional(readOnly=true)
	public List<Ride> getRideByUserId(Long user_id){
		return repo.findByUserId(user_id);
	}
	
	public void deleteRide(Long rideId) {
		repo.deleteById(rideId);
	}

}

