package com.capstoneproject.service.impl;

import com.capstoneproject.dto.UserDto;
import com.capstoneproject.model.User;
import com.capstoneproject.repository.UserRepository;
import com.capstoneproject.service.UserService;
import com.capstoneproject.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User registerUser(UserDto userDTO, PasswordEncoder passwordEncoder) {
        Optional<User> userOptional = userRepository.findByEmail(userDTO.getEmail());
        if (userOptional.isPresent()) {
            throw new RuntimeException("User already exists");
        }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return userRepository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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
