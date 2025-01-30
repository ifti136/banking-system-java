package com.yourname.banking.dao;

import com.yourname.banking.model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAO {
    private final AccountDAO accountDAO; // Add AccountDAO instance to fetch accounts

    public TransactionDAO() {
        new DBConnection();
        this.accountDAO = new AccountDAO(); // Initialize AccountDAO
    }

    // Save transaction to the database
    public boolean saveTransaction(Transaction transaction) {
        Connection connection = null;
        PreparedStatement pstmt = null;
        
        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);
            
            String query = "INSERT INTO transactions (id, accountId, amount, transactionType, timestamp) VALUES (?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(query);
            pstmt.setString(1, transaction.getId());
            pstmt.setString(2, transaction.getAccountId());
            pstmt.setDouble(3, transaction.getAmount());
            pstmt.setString(4, transaction.getTransactionType());
            pstmt.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));
            
            int result = pstmt.executeUpdate();
            connection.commit();
            return result > 0;
            
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error saving transaction: " + e.getMessage());
            return false;
        } finally {
            DBConnection.closeResources(connection, pstmt, null);
        }
    }

    //Find transaction by ID
    public Optional<Transaction> findTransactionById(String transactionId) {
        String query = "SELECT * FROM transactions WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getString("id"));
                transaction.setAccountId(rs.getString("accountId"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setTransactionType(rs.getString("transactionType"));
                transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                return Optional.of(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Retrieve all transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getString("id"));
                transaction.setAccountId(rs.getString("accountId"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setTransactionType(rs.getString("transactionType"));
                transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // Retrieve transactions by account ID
    public List<Transaction> getTransactionsByAccountId(String accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE accountId = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getString("id"));
                transaction.setAccountId(rs.getString("accountId"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setTransactionType(rs.getString("transactionType"));
                transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByCustomerId(String customerId) {
        if (customerId == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }

        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT t.* FROM transactions t " +
                      "JOIN accounts a ON t.accountId = a.id " +
                      "WHERE a.customerId = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Transaction transaction = new Transaction();
                transaction.setId(rs.getString("id"));
                transaction.setAccountId(rs.getString("accountId"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setTransactionType(rs.getString("transactionType"));
                transaction.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions by customer ID: " + e.getMessage());
        }
        return transactions;
    }
}