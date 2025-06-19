package com.user.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.user.service.Repo.UserRepo;
import com.user.service.jwt.JwtService;
import com.user.service.model.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
    private JwtService jwtService;

    public UserService(UserRepo userRepo, 
                     PasswordEncoder passwordEncoder,
                     JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(UserModel user) {
        if (userRepo.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user).getUserId() != null ? 
               "Registration successful" : "Registration failed";
    }

    public Map<String, Object> authenticate(UserModel user) {
        UserModel foundUser = userRepo.findByUsername(user.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        if (!passwordEncoder.matches(user.getPassword(), foundUser.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        
        // Generate token with user details
        String token = jwtService.generateToken(
            foundUser.getUsername(),
            new ArrayList<>(foundUser.getRoles()) // Convert Set<String> to List<String>
        );
        
        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", foundUser.getUsername());
        response.put("roles", foundUser.getRoles());
        response.put("expiresIn", jwtService.getExpirationDate());
        
        return response;
    }

    public String updateProfile(Long userId, UserModel updatedUser) {
        UserModel user = userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setUsername(updatedUser.getUsername());
        user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        // Add other fields as needed
        
        userRepo.save(user);
        return "Profile updated successfully";
    }

    public List<UserModel> getAllUsers() {
        return userRepo.findAll();
    }

    public void deleteUser(Long id) {
        if (!userRepo.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepo.deleteById(id);
    }
}