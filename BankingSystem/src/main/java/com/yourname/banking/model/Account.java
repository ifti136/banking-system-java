package com.yourname.banking.model;

import java.util.UUID;

public class Account {
    public enum AccountType {
        SAVINGS, FIXED_DEPOSIT
    }

    private String id;
    private String customerId;
    private double balance;
    private AccountType accountType;

    // Constructor with account type
    public Account(String customerId, double balance, AccountType accountType) {
        if (balance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        if (customerId == null || customerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be null or empty");
        }
        if (accountType == null) {
            throw new IllegalArgumentException("Account type cannot be null");
        }
        
        this.id = UUID.randomUUID().toString();
        this.customerId = customerId;
        this.balance = balance;
        this.accountType = accountType;
    }

    // Default constructor
    public Account() {
        this.id = UUID.randomUUID().toString();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    // Optional: toString for debugging
    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", customerId='" + customerId + '\'' +
                ", balance=" + balance +
                ", accountType=" + accountType +
                '}';
    }
}