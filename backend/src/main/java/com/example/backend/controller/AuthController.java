package com.example.backend.controller;

import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }
            
            User u = userRepo.findByUsername(user.getUsername());
            if (u != null && passwordEncoder.matches(user.getPassword(), u.getPassword())) {
                return ResponseEntity.ok("Login successful");
            } else {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            if (user.getUsername() == null || user.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }
            
            if (user.getUsername().trim().isEmpty() || user.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Username and password cannot be empty");
            }
            
            // Check if user already exists
            User existingUser = userRepo.findByUsername(user.getUsername());
            if (existingUser != null) {
                return ResponseEntity.status(409).body("Username already exists");
            }
            
            // Encode password before saving
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepo.save(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}

