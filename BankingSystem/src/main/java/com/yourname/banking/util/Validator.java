package com.yourname.banking.util;

import java.util.Optional;

import com.yourname.banking.dao.CustomerDAO;
import com.yourname.banking.dao.TransactionDAO;
import com.yourname.banking.model.Account;
import com.yourname.banking.model.Customer;
import com.yourname.banking.model.Transaction;

public class Validator {

    // Regular expressions for basic validation
    private static final CustomerDAO customerDAO = new CustomerDAO();
    private static final TransactionDAO transactionDAO = new TransactionDAO();

    public static boolean isValidCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }
        return isValidName(customer.getName())
                && isValidPhone(customer.getPhone())
                && isValidEmail(customer.getEmail())
                && isValidAddress(customer.getAddress());
    }

    private static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    // Validates phone number
    private static boolean isValidPhone(String phone) {
        return phone != null && !phone.trim().isEmpty();
    }

    // Validates email address
    private static boolean isValidEmail(String email) {
        return email != null && !email.trim().isEmpty();
    }

    // Checks if the address is non-null and non-empty (additional checks can be added if needed)
    private static boolean isValidAddress(String address) {
        return address != null && !address.trim().isEmpty();
    }

    // Validates the format and existence of a customer ID in the database
    public static boolean isValidCustomerId(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            System.out.println("Invalid Customer ID: ID is null or empty.");
            return false;
        }

        // Check if ID follows a specific pattern (e.g., UUID format)
        if (!isUUIDFormat(customerId)) {
            System.out.println("Invalid Customer ID: Incorrect format.");
            return false;
        }

        // Check if ID exists in the database
        Optional<?> customer = customerDAO.findCustomerById(customerId);
        if (customer.isEmpty()) {
            System.out.println("Invalid Customer ID: Customer not found in database.");
            return false;
        }

        return true;
    }

    // Validates the format and existence of a transaction ID in the database
    public static boolean isValidTransactionId(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            System.out.println("Invalid Transaction ID: ID is null or empty.");
            return false;
        }

        // Check if ID follows a specific pattern (e.g., UUID format)
        if (!isUUIDFormat(transactionId)) {
            System.out.println("Invalid Transaction ID: Incorrect format.");
            return false;
        }

        // Check if ID exists in the database
        Optional<?> transaction = transactionDAO.findTransactionById(transactionId);
        if (transaction.isEmpty()) {
            System.out.println("Invalid Transaction ID: Transaction not found in database.");
            return false;
        }

        return true;
    }

    // Checks if a string is in UUID format
    private static boolean isUUIDFormat(String id) {
        return id.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    public static boolean isValidString(String value) {
        return value != null && !value.trim().isEmpty();
    }

    // Validate if a number is positive (e.g., for balance and transaction amount)
    public static boolean isPositive(double value) {
        return value >= 0;
    }

    // Validate account type (additional check for AccountType enum)
    public static boolean isValidAccountType(Account.AccountType accountType) {
        return accountType != null;
    }

    // Validate Account ID (should be alphanumeric and not empty)
    public static boolean isValidAccountId(String accountId) {
        return accountId != null && !accountId.trim().isEmpty() && accountId.matches("^[a-zA-Z0-9]+$");
    }

    // Validate that balance is positive
    public static boolean isValidBalance(double balance) {
        return balance > 0;
    }

    // Validate transaction amount (must be positive)
    public static boolean isValidTransactionAmount(double amount) {
        return amount > 0;
    }

    // Check if account balance is sufficient for a withdrawal or transfer
    public static boolean hasSufficientBalance(Account account, double amount) {
        return account != null && account.getBalance() >= amount;
    }

    // Check if the account balance is zero for account closure
    public static boolean canCloseAccount(Account account) {
        return account != null && account.getBalance() == 0;
    }

    // Validate Customer details for creation
    public static boolean isValidCustomerDetails(String customerId, String name, String email) {
        return isValidCustomerId(customerId) && name != null && !name.trim().isEmpty() && isValidEmail(email);
    }

    // Validate Account creation
    public static boolean isValidAccountDetails(String customerId, double balance) {
        return isValidCustomerId(customerId) && isValidBalance(balance);
    }

    // Validate a transaction
    public static boolean isValidTransaction(Transaction transaction, Account fromAccount) {
        if (transaction == null) {
            return false;
        }

        // Ensure the transaction amount is valid
        if (!isValidTransactionAmount(transaction.getAmount())) {
            return false;
        }

        // Check for sufficient balance if the transaction is a withdrawal or transfer
        if (("WITHDRAWAL".equalsIgnoreCase(transaction.getTransactionType()) ||
                "TRANSFER".equalsIgnoreCase(transaction.getTransactionType())) &&
                !hasSufficientBalance(fromAccount, transaction.getAmount())) {
            return false;
        }

        return true;
    }

    public static boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty();
    }
}