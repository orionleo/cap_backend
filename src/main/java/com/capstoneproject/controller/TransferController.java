package com.capstoneproject.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.model.Account;
import com.capstoneproject.service.AccountService;
import com.capstoneproject.service.TransactionService;
import com.capstoneproject.util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final JwtUtil jwtUtil;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public TransferController(JwtUtil jwtUtil, AccountService accountService, TransactionService transactionService) {
        this.jwtUtil = jwtUtil;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/")
    public String welcomeToTransfer() {
        return "Welcome to the transfer api";
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> transferMoney(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        try {
            // Extract JWT token from Authorization header
            String token = authHeader.substring(7);
            // String userEmail = jwtUtil.extractEmail(token); // Extracting email (sub)
            // from the token

            // Extract account numbers and amount from the request body
            String fromAccountId = requestBody.get("fromAccountId").toString();
            String toAccountId = requestBody.get("toAccountId").toString();
            BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());

            // Fetch account details
            Optional<Account> fromAccountOptional = accountService.getAccountById(fromAccountId);
            Optional<Account> toAccountOptional = accountService.getAccountById(toAccountId);

            // Handle case where account is not found
            if (fromAccountOptional.isEmpty()) {
                response.put("error", "From account not found.");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            if (toAccountOptional.isEmpty()) {
                response.put("error", "To account not found.");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            Account fromAccount = fromAccountOptional.get();
            Account toAccount = toAccountOptional.get();
            // Check if accounts belong to the same person (user ID based)
            if (fromAccount.getUserId().equals(toAccount.getUserId())) {
                response.put("error", "Please redicrect to self-transfer.");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            // Check if the 'from' account belongs to the JWT user (based on user ID)
            String userIdFromJwt = jwtUtil.extractUserId(token); // Assuming JWT contains user ID and this method
                                                                 // extracts it
            if (!fromAccount.getUserId().equals(userIdFromJwt)) {
                response.put("error", "Unauthorized transfer attempt from another user's account.");
                response.put("success", false);
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(response);
            }

            // Check if 'from' account has sufficient balance
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                response.put("error", "Insufficient balance.");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            // Process the money transfer
            transactionService.transferMoney(fromAccount, toAccount, amount);

            // Prepare response data
            data.put("fromAccount", fromAccountId);
            data.put("toAccount", toAccountId);
            data.put("remainingBalance", fromAccount.getBalance());
            data.put("transferId", transactionService.getLastTransactionId()); // Assuming a method that returns the
                                                                               // latest transaction ID

            response.put("error", null);
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // In case of any exception, return an error response
            response.put("error", "An error occurred while processing the transaction: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/self")
    public ResponseEntity<Map<String, Object>> selfTransferMoney(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        try {
            // Extract JWT token from Authorization header
            String token = authHeader.substring(7);
            // String userEmail = jwtUtil.extractEmail(token); // Extracting email (sub)
            // from the token

            // Extract account numbers and amount from the request body
            String fromAccountId = requestBody.get("fromAccountId").toString();
            String toAccountId = requestBody.get("toAccountId").toString();
            BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());

            // Fetch account details
            Optional<Account> fromAccountOptional = accountService.getAccountById(fromAccountId);
            Optional<Account> toAccountOptional = accountService.getAccountById(toAccountId);

            // Handle case where account is not found
            if (fromAccountOptional.isEmpty()) {
                response.put("error", "From account not found.");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            if (toAccountOptional.isEmpty()) {
                response.put("error", "To account not found.");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            Account fromAccount = fromAccountOptional.get();
            Account toAccount = toAccountOptional.get();
            // Check if the 'from' account belongs to the JWT user (based on user ID)
            String userIdFromJwt = jwtUtil.extractUserId(token); // Assuming JWT contains user ID and this method
                                                                 // extracts it
            if (!fromAccount.getUserId().equals(userIdFromJwt) || !toAccount.getUserId().equals(userIdFromJwt)) {
                response.put("error", "Unauthorized transfer attempt from another user's account.");
                response.put("success", false);
                response.put("data", null);
                return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body(response);
            }
            // Check if accounts belong to the same person (user ID based)
            if (!fromAccount.getUserId().equals(toAccount.getUserId())) {
                response.put("error", "The accounts don't belong to the same user.");
                response.put("success", false);
                response.put("data", null);
                return ResponseEntity.badRequest().body(response);
            }

            // Check if 'from' account has sufficient balance
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                response.put("error", "Insufficient balance.");
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }

            // Process the money transfer
            transactionService.transferMoney(fromAccount, toAccount, amount);

            // Prepare response data
            data.put("fromAccount", fromAccountId);
            data.put("toAccount", toAccountId);
            data.put("remainingBalance", fromAccount.getBalance()); // Subtract balance after transfer
            data.put("transferId", transactionService.getLastTransactionId()); // Assuming a method that returns the
                                                                               // latest transaction ID

            response.put("error", null);
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // In case of any exception, return an error response
            response.put("error", "An error occurred while processing the transaction: " + e.getMessage());
            response.put("success", false);
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
