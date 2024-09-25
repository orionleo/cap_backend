package com.capstoneproject.service;

import java.util.Optional;

import com.capstoneproject.dto.UserDto;
import com.capstoneproject.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface UserService {
    User registerUser(UserDto userDTO ,PasswordEncoder passwordEncoder);  // Register a new user using UserDTO
    Optional<User> findByEmail(String email);  // Find a user by their email
    String authenticateUser(UserDto userDTO);
}
