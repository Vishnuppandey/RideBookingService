package com.ride.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ride.model.Ride;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

	@Query("SELECT r FROM Ride r WHERE r.user_id = :userId")
	List<Ride> findByUserId(@Param("userId") Long userId);

}
