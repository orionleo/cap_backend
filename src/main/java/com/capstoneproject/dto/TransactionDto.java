package com.capstoneproject.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class TransactionDto {
    private String id;
    private String fromAccountId; // ID of the source account
    private String toAccountId; // ID of the destination account
    private String fromUserEmail;
    private String toUserEmail;
    private BigDecimal amount; // Amount of the transaction
    private String transactionType; // Type of transaction (e.g., Credit, Debit)
    private Date timestamp; // Date and time of the transaction
    private String status; // Status of the transaction (e.g., Pending, Completed, Failed)
}