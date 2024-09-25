package com.capstoneproject.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.dto.UserDto;
import com.capstoneproject.model.User;
import com.capstoneproject.service.AuthService;
import com.capstoneproject.util.JwtUtil;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserDto userDTO) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Register the user
            User user = authService.registerUser(userDTO);

            // Generate JWT token
            String token = jwtUtil.generateToken(user);

            // Successful response
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);

            response.put("error", null);
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // If user already exists
            response.put("error", "User already exists.");
            response.put("success", false);
            response.put("data", null);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/")
    public String welcometoAuth() {
        return new String("Welcome to the auth api");
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody UserDto userDTO) {

        Map<String, Object> response = new HashMap<>();

        try {
            String token = authService.authenticateUser(userDTO);
            Map<String,Object> data = new HashMap<>();

            data.put("token", token);
            data.put("email", userDTO.getEmail());

            response.put("error", null);
            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // TODO: handle exception
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // Convert User model to UserDTO
    private UserDto convertToDTO(User user) {
        UserDto userDTO = new UserDto();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        return userDTO;
    }

}
