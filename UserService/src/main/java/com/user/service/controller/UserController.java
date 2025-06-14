package com.user.service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.user.service.jwt.JwtService;
import com.user.service.model.UserModel;
import com.user.service.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService service;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UserModel> registerUser(@RequestBody UserModel user) {
        logger.info("Registering user: {}", user.getUsername());

        try {
            String result = service.register(user);
            if (result.equals("User registered successfully!")) {
                logger.info("User registered successfully: {}", user.getUsername());
                return ResponseEntity.ok(user);
            } else {
                logger.warn("User registration failed for {}: {}", user.getUsername(), result);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping("/update-user/{userid}")
    public ResponseEntity<String> updateUser(@PathVariable Long userid, @RequestBody UserModel user) {
        logger.info("Updating user with ID: {}", userid);

        String result = service.updateProfile(userid, user);
        if (result.equals("Profile updated successfully!")) {
            logger.info("User profile updated successfully: {}", userid);
            return ResponseEntity.ok(result);
        } else {
            logger.warn("User profile update failed for {}: {}", userid, result);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-allUser")
    public List<UserModel> findAllUser() {
        logger.info("Fetching all users...");
        List<UserModel> users = service.userDetails();
        logger.info("Total users retrieved: {}", users.size());
        return users;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        logger.info("Deleting user with ID: {}", id);
        service.deleteUSer(id);
        logger.info("User deleted successfully: {}", id);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserModel user) {
        logger.info("Attempting login for user: {}", user.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        if (passwordEncoder.matches(user.getPassword(), userDetails.getPassword())) {
            String role = user.getRoles() != null && !user.getRoles().isEmpty() 
                          ? user.getRoles().iterator().next() 
                          : "USER"; // Default role if missing
            
            String token = jwtService.generateToken(userDetails);
            logger.info("Login successful for user: {} | Role: {}", user.getUsername(), role);
            return ResponseEntity.ok(token);
        }

        logger.warn("Invalid credentials for user: {}", user.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
