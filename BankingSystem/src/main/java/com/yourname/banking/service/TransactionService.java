package com.yourname.banking.service;

import com.yourname.banking.dao.TransactionDAO;
import com.yourname.banking.model.Account;
import com.yourname.banking.model.Transaction;
import com.yourname.banking.util.Validator;

import java.util.List;

public class TransactionService {
    private TransactionDAO transactionDAO = new TransactionDAO();

    // Save transaction
    public boolean saveTransaction(Transaction transaction) {
        return transactionDAO.saveTransaction(transaction);
    }

    // Get transaction by ID
    public Transaction getTransactionById(String transactionId) {
        return transactionDAO.findTransactionById(transactionId).orElse(null);
    }

    // Get all transactions for a specific account
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        return transactionDAO.getTransactionsByAccountId(accountId);
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionDAO.getAllTransactions();
    }

    public boolean processTransaction(Transaction transaction, Account fromAccount) {
        if (!Validator.isValidTransaction(transaction, fromAccount)) {
            throw new IllegalArgumentException("Invalid transaction. Check transaction type, amount, and account balance.");
        }

        // Update account balance based on transaction type
        double newBalance;
        if ("DEPOSIT".equals(transaction.getTransactionType())) {
            newBalance = fromAccount.getBalance() + transaction.getAmount();
        } else if ("WITHDRAWAL".equals(transaction.getTransactionType())) {
            newBalance = fromAccount.getBalance() - transaction.getAmount();
        } else {
            throw new IllegalArgumentException("Invalid transaction type");
        }

        // Save transaction and update balance
        return transactionDAO.saveTransaction(transaction);
    }

    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        return transactionDAO.getTransactionsByCustomerId(customerId);
    }
}