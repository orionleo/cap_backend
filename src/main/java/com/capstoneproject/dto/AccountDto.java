package com.capstoneproject.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDto {
    private String id;
    private String userId; // ID of the user associated with the account
    private String accountNumber; // Unique account number
    private BigDecimal balance; // Current balance of the account
    private String userEmail;
}