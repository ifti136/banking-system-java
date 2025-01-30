package com.yourname.banking.dao;

import com.yourname.banking.model.Customer;
import com.yourname.banking.util.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDAO {
    public CustomerDAO() {
        new DBConnection();
    }

    // Save customer to the database
    public boolean saveCustomer(Customer customer) {
        if (customer == null) {
            System.err.println("Customer object is null");
            return false;
        }

        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false); // Start transaction

            String query = "INSERT INTO customers (id, name, phone, address, email, password) VALUES (?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(query);

            // Add debug logging
            System.out.println("Saving customer: " + customer);
            
            pstmt.setString(1, customer.getId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getAddress());
            pstmt.setString(5, customer.getEmail());
            pstmt.setString(6, customer.getPassword());
            
            int result = pstmt.executeUpdate();
            connection.commit(); // Commit transaction
            return result > 0;

        } catch (SQLException e) {
            try {
                if (connection != null)
                    connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
            System.err.println("Error saving customer: " + e.getMessage());
            e.printStackTrace(); // Add full stack trace
            return false;
        } finally {
            DBConnection.closeResources(connection, pstmt, null);
        }
    }

    // Update customer information
    public boolean updateCustomer(Customer customer) {
        if (customer == null || !Validator.isValidString(customer.getId()) ||
                !Validator.isValidString(customer.getName()) ||
                !Validator.isValidString(customer.getEmail())) {
            System.out.println("Invalid customer data.");
            return false;
        }

        String query = "UPDATE customers SET name = ?, address = ?, email = ?, phone = ?, password = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getAddress());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPhone());
            pstmt.setString(5, customer.getPassword());
            pstmt.setString(6, customer.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Find customer by ID
    public Optional<Customer> findCustomerById(String customerId) {
        String query = "SELECT * FROM customers WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Create Customer instance from result set
                Customer customer = new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("password"));
                return Optional.of(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Retrieve all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT * FROM customers";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Create Customer instance for each row
                Customer customer = new Customer(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("email"),
                        rs.getString("password"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Delete customer by ID
    public boolean deleteCustomer(String customerId) {
        String query = "DELETE FROM customers WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomerPassword(String customerId, String newPassword) {
        String query = "UPDATE customers SET password = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}