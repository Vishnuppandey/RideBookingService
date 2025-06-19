package com.user.service.Repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user.service.model.UserModel;


@Repository
public interface UserRepo extends JpaRepository<UserModel, Long>  {
	
	Optional<UserModel> findByUsername(String username);

}
