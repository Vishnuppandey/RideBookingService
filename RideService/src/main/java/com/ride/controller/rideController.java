package com.ride.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ride.dto.RideDto;
import com.ride.model.Ride;
import com.ride.service.rideService;

@RestController
@RequestMapping("/rides")
public class rideController {

	
	@Autowired
	private rideService service;
	
	@PostMapping("/request")
	public ResponseEntity<Ride> createRide(@RequestBody RideDto ride){
		Ride createdRide=service.createRide(ride);
		return ResponseEntity.ok(createdRide);
	}
	
	@GetMapping("/{rideId}")
	public ResponseEntity<List<Ride>> getRideById(@PathVariable Long rideId){
		List<Ride> rides=service.getRideByUserId(rideId);
		return  ResponseEntity.ok(rides); 
	}
	
	@PutMapping("/{rideId}/status")
	public ResponseEntity<Ride> updateRideStatus(@PathVariable Long rideId, @RequestParam String status){
		Ride updatedRide=service.updateRideStatus(rideId, status);
		return ResponseEntity.ok(updatedRide);
	}
	
	@PutMapping("/{rideId}/assign-driver")
	public ResponseEntity<Ride> assignDriver(@PathVariable Long rideId, @RequestBody Map<String, Integer> requestBody) {
	    Integer driverId = requestBody.get("driverId");
	    Ride ride = service.assignDriver(rideId, driverId);
	    return ResponseEntity.ok(ride);
	}

	@GetMapping("user/{userId}")
	public ResponseEntity<List<Ride>> getAllRideByUserId(@PathVariable Long user_id){
		List<Ride> allRide=service.getRideByUserId(user_id);
		return ResponseEntity.ok(allRide);
	}
	
	@DeleteMapping("/{rideId}")
	public ResponseEntity<Void> deleteRide(@PathVariable Long rideId){
		service.deleteRide(rideId);
		return ResponseEntity.noContent().build();
	}
}
