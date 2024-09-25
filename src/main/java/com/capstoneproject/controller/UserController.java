package com.capstoneproject.controller;

import com.capstoneproject.model.Transaction;
import com.capstoneproject.model.Account;
import com.capstoneproject.service.AccountService;
import com.capstoneproject.service.TransactionService;
import com.capstoneproject.util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    // 1. API to Get List of All User Transactions with Unique Transactions
    @GetMapping("/transactions")
    public ResponseEntity<Map<String, Object>> getAllUserTransactions(
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Extract JWT token from Authorization header
            String token = authHeader.substring(7);

            // Extract userId from the JWT
            String userId = jwtUtil.extractUserId(token);

            // Retrieve all accounts for the user
            List<Account> userAccounts = accountService.getAccountsByUser(userId);

            // Collect all transactions into a single list
            Map<String, Object> data = new HashMap<>();
            List<Transaction> allTransactions = new ArrayList<>();
            for (Account account : userAccounts) {
                List<Transaction> transactions = transactionService.getMergedTransactionHistory(account.getId());
                allTransactions.addAll(transactions);
            }

            // Remove duplicate transactions (e.g., self-transfers)
            List<Transaction> uniqueTransactions = allTransactions.stream()
                    .distinct()  // Removes duplicates based on the equals() and hashCode() methods of Transaction
                    .collect(Collectors.toList());

            data.put("transactions", uniqueTransactions);
            data.put("transactionLength", uniqueTransactions.size());
            response.put("success", true);
            response.put("error", null);
            response.put("data", data);  // All unique transactions

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<Map<String, Object>> getAllUserAccounts(
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Extract JWT token from Authorization header
            String token = authHeader.substring(7);

            // Extract userId from the JWT
            String userId = jwtUtil.extractUserId(token);

            // Retrieve all accounts for the user
            List<Account> userAccounts = accountService.getAccountsByUser(userId);

            if (userAccounts.isEmpty()) {
                response.put("success", false);
                response.put("error", "No accounts found for the user.");
                response.put("data", null);
                return ResponseEntity.status(HttpServletResponse.SC_NOT_FOUND).body(response);
            }

            // Prepare success response
            response.put("success", true);
            response.put("error", null);
            response.put("data", userAccounts);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 2. API to Generate a PDF of All Unique Transactions (to be implemented similarly)
    // @GetMapping("/transactions/pdf")
    // public void generateTransactionsPdf(
    //         @RequestHeader("Authorization") String authHeader,
    //         HttpServletResponse response) throws IOException {
    //     try {
    //         // Extract JWT token from Authorization header
    //         String token = authHeader.substring(7);

    //         // Extract userId from the JWT
    //         String userId = jwtUtil.extractUserId(token);

    //         // Retrieve all accounts for the user
    //         List<Account> userAccounts = transactionService.getAccountsByUserId(userId);

    //         // Collect all transactions into a single list
    //         List<Transaction> allTransactions = new ArrayList<>();
    //         for (Account account : userAccounts) {
    //             List<Transaction> transactions = transactionService.getMergedTransactionHistory(account.getId());
    //             allTransactions.addAll(transactions);
    //         }

    //         // Remove duplicate transactions
    //         List<Transaction> uniqueTransactions = allTransactions.stream()
    //                 .distinct()
    //                 .collect(Collectors.toList());

    //         // Set PDF response headers
    //         response.setContentType("application/pdf");
    //         response.setHeader("Content-Disposition", "attachment; filename=transactions.pdf");

    //         // Generate PDF document using iText (or other library)

    //     } catch (Exception e) {
    //         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    //         response.getWriter().write("Error generating PDF: " + e.getMessage());
    //     }
    // }
}
