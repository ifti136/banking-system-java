package com.yourname.banking.service;

import com.yourname.banking.dao.AccountDAO;
import com.yourname.banking.model.Account;

import java.util.List;
import java.util.Optional;

public class AccountService {
    private AccountDAO accountDAO;

    public AccountService() {
        this.accountDAO = new AccountDAO();
    }

    // Save account with validation
    public boolean saveAccount(Account account) {
        List<Account> customerAccounts = accountDAO.getAccountsByCustomerId(account.getCustomerId());

        // Check if the customer already has two accounts
        if (customerAccounts.size() >= 2) {
            System.out.println("Customer already has the maximum number of accounts.");
            return false;
        }

        // Check if the customer already has an account of the specified type
        boolean hasAccountType = customerAccounts.stream()
            .anyMatch(acc -> acc.getAccountType() == account.getAccountType());
        if (hasAccountType) {
            System.out.println("Customer already has an account of type: " + account.getAccountType());
            return false;
        }

        // Save the account if validations pass
        return accountDAO.saveAccount(account);
    }

    public Optional<Account> findAccountById(String accountId) {
        return accountDAO.findAccountById(accountId);
    }

    public boolean updateAccountBalance(String accountId, double newBalance) {
        return accountDAO.updateAccountBalance(accountId, newBalance);
    }

    public boolean deleteAccount(String accountId) {
        return accountDAO.deleteAccount(accountId);
    }

    public List<Account> getAllAccounts() {
        return accountDAO.getAllAccounts();
    }

    public List<Account> getAccountsByCustomerId(String customerId){
        return accountDAO.getAccountsByCustomerId(customerId);
    }
}