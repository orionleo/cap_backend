package com.capstoneproject.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capstoneproject.model.Account;
import com.capstoneproject.model.User;
import com.capstoneproject.repository.AccountRepository;
import com.capstoneproject.service.AccountService;

import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account createAccount(User user, Account account) {
        account.setUserId(user.getId()); // Set the user for the account
        System.out.println("USER " + user);
        account.setUserEmail(user.getUsername());
        return accountRepository.save(account); // Save and return the created account
    }

    @Override
    public List<Account> getAccountsByUser(String userId) {
        return accountRepository.findByUserId(userId); // Retrieve all accounts associated with a user
    }

    @Override
    public Optional<Account> getAccountById(String accountId) {
        return accountRepository.findById(accountId);
    }

    @Transactional
    @Override
    public void transferFunds(String fromAccountId, String toAccountId, double amount) {
        Optional<Account> fromAccountOpt = accountRepository.findById(fromAccountId);
        Optional<Account> toAccountOpt = accountRepository.findById(toAccountId);

        if (fromAccountOpt.isEmpty() || toAccountOpt.isEmpty()) {
            throw new IllegalArgumentException("One or both accounts not found");
        }

        Account fromAccount = fromAccountOpt.get();
        Account toAccount = toAccountOpt.get();

        // Convert amount to BigDecimal
        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        // Use BigDecimal's compareTo for comparison
        if (fromAccount.getBalance().compareTo(transferAmount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in the source account");
        }

        // Perform the funds transfer using BigDecimal
        fromAccount.setBalance(fromAccount.getBalance().subtract(transferAmount));
        toAccount.setBalance(toAccount.getBalance().add(transferAmount));

        // Save the updated account details
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }
}
