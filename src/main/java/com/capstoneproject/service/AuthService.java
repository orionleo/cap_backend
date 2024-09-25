package com.capstoneproject.service;

import com.capstoneproject.dto.UserDto;
import com.capstoneproject.model.User;

public interface AuthService {
    String authenticateUser(UserDto userDTO);

    User registerUser(UserDto userDTO); // Register a new user using UserDTO
}
