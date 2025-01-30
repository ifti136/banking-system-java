package com.yourname.banking.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private String id; // Transaction ID
    private String accountId; // Associated Account ID
    private double amount; // Amount for the transaction
    private String transactionType; // Type of transaction (e.g., deposit, withdrawal)
    private LocalDateTime timestamp; // Timestamp of the transaction

    // Default constructor
    public Transaction() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }

    // Constructor with parameters
    public Transaction(String accountId, double amount, String transactionType) {
        this.id = UUID.randomUUID().toString();
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}