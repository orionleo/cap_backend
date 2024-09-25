package com.capstoneproject.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import com.capstoneproject.model.User;
import com.capstoneproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.model.Account;
import com.capstoneproject.repository.AccountRepository;
import com.capstoneproject.util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import javax.swing.text.html.Option;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String welcomeToAccount() {
        return new String("Welcome to account api");
    }

    @PostMapping("/new")
    public ResponseEntity<Map<String, Object>> createNewAccount(@RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        try {
            // Extract JWT token from Authorization header
            String token = authHeader.substring(7);

            String accountType = requestBody.get("accountType").toString();
            BigDecimal balance = new BigDecimal(requestBody.get("balance").toString());

            Random random = new Random();
            char[] digits = new char[12];
            digits[0] = (char) (random.nextInt(9) + '1');
            for (int i = 1; i < 12; i++) {
                digits[i] = (char) (random.nextInt(10) + '0');
            }
            String accountNumber = (new String(digits));

            String userIdFromJwt = jwtUtil.extractUserId(token);

            Account newAccount = new Account();

            Optional<User> optionalAccount = userRepository.findById(userIdFromJwt);
            if(optionalAccount.isEmpty()){
                throw new RuntimeException("User not found");
            }
            newAccount.setAccountNumber(accountNumber);
            newAccount.setAccountType(accountType);
            newAccount.setBalance(balance);
            newAccount.setUserId(userIdFromJwt);
            newAccount.setUserEmail(optionalAccount.get().getUsername());

            Account newCreatedAccount = accountRepository.save(newAccount);

            data.put("account", newCreatedAccount);

            response.put("error", null);
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<Map<String, Object>> updateAccount(@RequestBody Map<String, Object> requestBody,
            @RequestHeader("Authorization") String authHeader) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();

        try {
            // Extract JWT token from Authorization header
            String token = authHeader.substring(7);

            // Extract the user ID from the token
            String userIdFromJwt = jwtUtil.extractUserId(token);

            // Extract accountNumber from the request
            String accountNumber = requestBody.get("accountNumber").toString();

            // Find the account by accountNumber
            Optional<Account> optionalAccount = accountRepository.findById(accountNumber);

            if (!optionalAccount.isPresent()) {
                throw new IllegalArgumentException("Account not found");
            }

            Account account = optionalAccount.get();
            // Ensure the account belongs to the user making the request
            if (!account.getUserId().equals(userIdFromJwt)) {
                throw new IllegalArgumentException("Unauthorized to update this account");
            }

            // Update the account type if provided in the request
            if (requestBody.containsKey("accountType")) {
                String accountType = requestBody.get("accountType").toString();
                account.setAccountType(accountType);
            }

            // Update the balance if provided in the request
            if (requestBody.containsKey("balance")) {
                BigDecimal balance = new BigDecimal(requestBody.get("balance").toString());
                account.setBalance(balance);
            }

            // Save the updated account
            Account updatedAccount = accountRepository.save(account);

            data.put("account", updatedAccount);

            response.put("error", null);
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
