package com.yourname.banking.dao;

import com.yourname.banking.model.Account;
import com.yourname.banking.model.Account.AccountType;
import com.yourname.banking.util.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountDAO {
    public AccountDAO() {
        new DBConnection();
    }

    // Save account to the database with validation
    public boolean saveAccount(Account account) {
        if (account == null ||
                !Validator.isValidString(account.getCustomerId()) ||
                !Validator.isPositive(account.getBalance()) ||
                !Validator.isValidAccountType(account.getAccountType())) {
            System.out.println("Invalid account data.");
            return false;
        }

        String query = "INSERT INTO accounts (id, customerId, balance, accountType) VALUES (?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, account.getId());
            pstmt.setString(2, account.getCustomerId());
            pstmt.setDouble(3, account.getBalance());
            pstmt.setString(4, account.getAccountType().name());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update account balance
    public boolean updateAccountBalance(String accountId, double newBalance) {
        String query = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Find account by ID
    public Optional<Account> findAccountById(String accountId) {
        String query = "SELECT * FROM accounts WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Account account = new Account();
                account.setId(rs.getString("id"));
                account.setCustomerId(rs.getString("customerId"));
                account.setBalance(rs.getDouble("balance"));
                account.setAccountType(AccountType.valueOf(rs.getString("accountType")));
                return Optional.of(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Account> getAccountsByCustomerId(String customerId) {
    List<Account> accounts = new ArrayList<>();
    String query = "SELECT * FROM accounts WHERE customerId = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement pstmt = connection.prepareStatement(query)) {
        pstmt.setString(1, customerId);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            Account account = new Account();
            account.setId(rs.getString("id"));
            account.setCustomerId(rs.getString("customerId"));
            account.setBalance(rs.getDouble("balance"));
            account.setAccountType(AccountType.valueOf(rs.getString("accountType")));
            accounts.add(account);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return accounts;
}


    // Retrieve all accounts
    public List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String query = "SELECT * FROM accounts";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Account account = new Account();
                account.setId(rs.getString("id"));
                account.setCustomerId(rs.getString("customerId"));
                account.setBalance(rs.getDouble("balance"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    // Delete account by ID
    public boolean deleteAccount(String accountId) {
        String query = "DELETE FROM accounts WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, accountId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}