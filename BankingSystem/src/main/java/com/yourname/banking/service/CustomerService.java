package com.yourname.banking.service;

import com.yourname.banking.dao.CustomerDAO;
import com.yourname.banking.model.Customer;
import com.yourname.banking.util.Validator;

import java.util.List;
import java.util.Optional;

public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    // Retrieve customer by ID
    public Optional<Customer> getCustomerById(String customerId) {
        return customerDAO.findCustomerById(customerId);
    }

    // Retrieve all customers
    public List<Customer> getAllCustomers() {
        return customerDAO.getAllCustomers();
    }

    // Save customer
    public boolean saveCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }
        
        // Basic email validation
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (customer.getEmail() == null || !customer.getEmail().matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        return customerDAO.saveCustomer(customer);
    }

    // Update customer
    public boolean updateCustomer(Customer customer) {
        if (!Validator.isValidCustomerDetails(customer.getId(), customer.getName(), customer.getEmail())) {
            throw new IllegalArgumentException(
                    "Invalid customer details. Please check the customer ID, name, and email format.");
        }
        return customerDAO.updateCustomer(customer);
    }

    // Delete customer
    public boolean deleteCustomer(String customerId) {
        return customerDAO.deleteCustomer(customerId);
    }

    public boolean createCustomer(String customerId, String name, String email, String phone, String password) {
        if (!Validator.isValidCustomerDetails(customerId, name, email)) {
            throw new IllegalArgumentException(
                    "Invalid customer details. Please check the customer ID, name, and email format.");
        }
        Customer customer = new Customer(customerId, name, email, phone, password);
        return customerDAO.saveCustomer(customer);
    }
    
    public boolean updateCustomerPassword(String customerId, String newPassword) {
        return customerDAO.updateCustomerPassword(customerId, newPassword);
    }

    public boolean verifyCustomerCredentials(String customerId, String password) {
        Optional<Customer> customerOpt = customerDAO.findCustomerById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            return customer.getPassword().equals(password);
        }
        return false;
    }
}