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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

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
    @GetMapping("/transactions/pdf")
    public void generateTransactionsPdf(
            @RequestHeader("Authorization") String authHeader,
            HttpServletResponse response) throws IOException {
        try {
            // Extract JWT token from Authorization header
            String token = authHeader.substring(7);

            // Extract userId from the JWT
            String userId = jwtUtil.extractUserId(token);

            // Retrieve all accounts for the user
            List<Account> userAccounts = accountService.getAccountsByUser(userId);

            // Collect all transactions into a single list
            List<Transaction> allTransactions = new ArrayList<>();
            for (Account account : userAccounts) {
                List<Transaction> transactions = transactionService.getMergedTransactionHistory(account.getId());
                allTransactions.addAll(transactions);
            }

            // Remove duplicate transactions
            List<Transaction> uniqueTransactions = allTransactions.stream()
                    .distinct()
                    .collect(Collectors.toList());

            System.out.println(uniqueTransactions);

            // Set PDF response headers
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=transactions.pdf");

            // Generate PDF document using iText (or other library)
            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());

            document.open();

            // Add title
            Font font = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Paragraph title = new Paragraph("Transaction History", font);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n")); // Add a blank line

            // Create a table with 5 columns: ID, FromAccount, ToAccount, Amount, Date
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Add table headers
            addTableHeader(table);

            // Add transactions to the table
            for (Transaction txn : uniqueTransactions) {
                addTransactionToTable(table, txn);
            }

            // Add the table to the document
            document.add(table);

            document.close();


        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error generating PDF: " + e.getMessage());
        }
    }

    // Helper method to add table headers
    private void addTableHeader(PdfPTable table) {
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        PdfPCell header;

        header = new PdfPCell(new Phrase("ID", headerFont));
        table.addCell(header);

        header = new PdfPCell(new Phrase("From User", headerFont));
        table.addCell(header);

//        header = new PdfPCell(new Phrase("From Account", headerFont));
//        table.addCell(header);

        header = new PdfPCell(new Phrase("To User", headerFont));
        table.addCell(header);

//        header = new PdfPCell(new Phrase("To Account", headerFont));
//        table.addCell(header);

        header = new PdfPCell(new Phrase("Type", headerFont));
        table.addCell(header);

        header = new PdfPCell(new Phrase("Amount", headerFont));
        table.addCell(header);

        header = new PdfPCell(new Phrase("Date", headerFont));
        table.addCell(header);
    }

    // Helper method to add a transaction row to the table
    private void addTransactionToTable(PdfPTable table, Transaction txn) {
        table.addCell(txn.getId()); // Transaction ID
        table.addCell(txn.getFromUserEmail());
//        table.addCell(txn.getFromAccountId()); // From account ID
        table.addCell(txn.getToUserEmail());
//        table.addCell(txn.getToAccountId()); // To account ID
        table.addCell(txn.getTransactionType());
        table.addCell(txn.getAmount().toString()); // Transaction amount
        table.addCell(txn.getTimestamp().toString()); // Transaction date
    }
}
