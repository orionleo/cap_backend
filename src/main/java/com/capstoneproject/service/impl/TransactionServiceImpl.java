package com.capstoneproject.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.capstoneproject.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capstoneproject.model.Account;
import com.capstoneproject.model.Transaction;
import com.capstoneproject.repository.TransactionRepository;
import com.capstoneproject.repository.AccountRepository;
import com.capstoneproject.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository; // Assuming you have this repository for updating account balances

    @Override
    public Transaction recordTransaction(Transaction transaction) {
        return transactionRepository.save(transaction); // Save and return the recorded transaction
    }

    @Override
    public List<Transaction> getMergedTransactionHistory(String accountId) {
        // Retrieve transactions associated with the account ID
        List<Transaction> fromTransactions = transactionRepository.findByFromAccountId(accountId);
        List<Transaction> toTransactions = transactionRepository.findByToAccountId(accountId);

        List<Transaction> combinedTransactions = fromTransactions;
        combinedTransactions.addAll(toTransactions);

        // Sort combined list by timestamp
        return combinedTransactions.stream()
                .sorted((t1, t2) -> t1.getTimestamp().compareTo(t2.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public void transferMoney(Account fromAccount, Account toAccount, BigDecimal amount) {
        // Ensure 'from' account has sufficient balance
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance in the source account");
        }

        // Subtract the amount from the 'from' account
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));

        // Add the amount to the 'to' account
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Save the updated account balances
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);


        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setFromAccountId(fromAccount.getId());
        transaction.setToAccountId(toAccount.getId());
        transaction.setFromUserEmail(fromAccount.getUserEmail()); // Set the fromUserEmail
        transaction.setToUserEmail(toAccount.getUserEmail()); // Set the fromUserEmail
        transaction.setAmount(amount);
        transaction.setTimestamp(new java.util.Date());
        transaction.setStatus("COMPLETED");
        transaction.setFromUserId(fromAccount.getUserId().toString());
        transaction.setToUserId(toAccount.getUserId().toString());
        if (fromAccount.getUserId().equals(toAccount.getUserId()))
            transaction.setTransactionType("SELF-TRANSFER");
        else
            transaction.setTransactionType("TRANSFER");
        // Save the transaction in the database
        transactionRepository.save(transaction);
    }

    @Override
    public String getLastTransactionId() {
        // Get the last transaction by ordering by the timestamp or ID
        Optional<Transaction> lastTransaction = transactionRepository.findTopByOrderByTimestampDesc();
        return lastTransaction.map(Transaction::getId).orElse(null); // Return the ID if present, else null
    }
}
