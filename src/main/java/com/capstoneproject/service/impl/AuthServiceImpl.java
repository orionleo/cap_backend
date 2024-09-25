package com.capstoneproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capstoneproject.dto.UserDto;
import com.capstoneproject.model.User;
import com.capstoneproject.repository.UserRepository;
import com.capstoneproject.service.AuthService;
import com.capstoneproject.util.JwtUtil;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public User registerUser(UserDto userDTO) {
        // Check if the user already exists
        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());
        if (userOptional.isPresent()) {
            throw new RuntimeException("User already exists.");
        }

        // Create a new user
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        // Save the user to the database
        return userRepository.save(user);
    }

    @Override
    public String authenticateUser(UserDto userDTO) {
        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Check if the provided password matches the stored one
            if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }

            // Generate JWT token
            String token = jwtUtil.generateToken(user);
            return token;
        }
        // If authentication fails, return null or throw an exception
        throw new RuntimeException("Invalid email or password.");
    }
}
