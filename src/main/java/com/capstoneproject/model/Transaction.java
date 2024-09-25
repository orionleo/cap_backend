package com.capstoneproject.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "transactions")
public class Transaction {
    @Id
    private String id;
    private String fromAccountId;
    private String toAccountId;
    private String fromUserEmail;
    private String toUserEmail;
    private BigDecimal amount;
    private String transactionType; // e.g., Credit, Debit
    private Date timestamp;
    private String status; // e.g., Pending, Completed, Failed
    private String fromUserId;
    private String toUserId;
}