package com.capstoneproject.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String email; // User's email address
    private String password; // User's password (hashed in real applications)
}
