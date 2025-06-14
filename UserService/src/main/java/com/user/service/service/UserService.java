package com.user.service.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.user.service.Repo.UserRepo;

import com.user.service.model.UserModel;

import com.user.service.security.SecurityConfiguration;

@Service
public class UserService {

	@Autowired
	private UserRepo repo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;


	public String register(UserModel user) {
        if (repo.findByUsername(user.getUsername()).isPresent()) {
            return "User already exists!";
        }

        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);
        return "User registered successfully!";
    }

	public String updateProfile(Long userId, UserModel updatedUser) {
		Optional<UserModel> existingUser = repo.findById(userId);
		if (existingUser.isPresent()) {
			UserModel user = existingUser.get();

			// Update fields based on userModel
			user.setUsername(updatedUser.getUsername());
			user.setFirstName(updatedUser.getFirstName());
			user.setLastName(updatedUser.getLastName());
			user.setRoles(new HashSet<>(updatedUser.getRoles()));



			user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

			repo.save(user);
			return "Profile updated successfully!";
		}
		return "User not found!";
	}

	public List<UserModel> userDetails() {

		return repo.findAll();
	}
	public void deleteUSer(Long id) {
		repo.deleteById(id);
	}
}
