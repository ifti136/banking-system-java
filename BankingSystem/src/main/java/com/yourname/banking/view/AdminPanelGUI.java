package com.yourname.banking.view;

import com.yourname.banking.model.Customer;
import com.yourname.banking.model.Account;
import com.yourname.banking.model.Transaction;
import com.yourname.banking.service.CustomerService;
import com.yourname.banking.service.AccountService;
import com.yourname.banking.service.TransactionService;
import com.yourname.banking.util.Validator;
import com.yourname.banking.util.DialogUtil;
import com.yourname.banking.util.AnimationUtil;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class AdminPanelGUI extends JFrame {

    // Styling Constants
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 16);

    // Services
    private final CustomerService customerService = new CustomerService();
    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();

    // Add these fields
    private boolean darkModeEnabled = false;

    public AdminPanelGUI() {
        // Frame setup
        setTitle("Admin Panel");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);

        // Title label
        JLabel titleLabel = new JLabel("=== Admin Panel ===", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        // Add space below title
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(10, 1, 10, 10));
        mainPanel.add(buttonPanel);

        // Add buttons
        addButton(buttonPanel, "Add Customer", e -> addCustomer());
        addButton(buttonPanel, "Delete Customer", e -> deleteCustomer());
        addButton(buttonPanel, "Get Customer By ID", e -> getCustomerById());
        addButton(buttonPanel, "Get All Customers", e -> getAllCustomers());
        addButton(buttonPanel, "Create Account", e -> createAccount());
        addButton(buttonPanel, "Delete Account", e -> deleteAccount());
        addButton(buttonPanel, "Get Accounts By Customer ID", e -> getAccountsByCustomerId());
        addButton(buttonPanel, "Retrieve Transaction History Of Customer", e -> viewTransactionHistory());
        addButton(buttonPanel, "Retrieve All Transactions", e -> viewAllTransactions());
        addButton(buttonPanel, "Exit", e -> dispose());

        // Add dark mode toggle
        addDarkModeToggle();

        setVisible(true);
    }

    // Utility method for adding buttons
    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        panel.add(button);
    }

    // Method to add a new customer
    private void addCustomer() {
        JDialog dialog = new JDialog(this, "Add Customer", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create input fields
        JTextField nameField = createStyledField("Name");
        JTextField phoneField = createStyledField("Phone");
        JTextField addressField = createStyledField("Address");
        JTextField emailField = createStyledField("Email");
        JPasswordField passwordField = (JPasswordField) createStyledField("Password");

        // Add fields to panel
        addFieldToPanel(panel, "Name:", nameField);
        addFieldToPanel(panel, "Phone:", phoneField);
        addFieldToPanel(panel, "Address:", addressField);
        addFieldToPanel(panel, "Email:", emailField);
        addFieldToPanel(panel, "Initial Password:", passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Customer");
        JButton cancelButton = new JButton("Cancel");

        styleButton(addButton);
        styleButton(cancelButton);

        addButton.addActionListener(e -> {
            if (validateCustomerInput(nameField, emailField, passwordField)) {
                try {
                    Customer customer = new Customer(
                            nameField.getText().trim(),
                            phoneField.getText().trim(),
                            addressField.getText().trim(),
                            emailField.getText().trim(),
                            new String(passwordField.getPassword()));

                    if (customerService.saveCustomer(customer)) {
                        dialog.dispose();
                        DialogUtil.showCustomMessageDialog(
                                this,
                                "Success",
                                String.format("Customer added successfully.\nCustomer ID: %s", customer.getId()),
                                "OK",
                                UIManager.getIcon("OptionPane.informationIcon"));
                    } else {
                        DialogUtil.showCustomMessageDialog(
                                this,
                                "Error",
                                "Failed to add customer.",
                                "OK",
                                UIManager.getIcon("OptionPane.errorIcon"));
                    }
                } catch (Exception ex) {
                    DialogUtil.showCustomMessageDialog(
                            this,
                            "Error",
                            "Error: " + ex.getMessage(),
                            "OK",
                            UIManager.getIcon("OptionPane.errorIcon"));
                }
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JTextField createStyledField(String name) {
        JTextField field = "Password".equals(name) ? new JPasswordField() : new JTextField();
        field.setColumns(20);
        field.setMaximumSize(new Dimension(300, 30));
        return field;
    }

    private void addFieldToPanel(JPanel panel, String labelText, JTextField field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        fieldPanel.setMaximumSize(new Dimension(400, 35));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));

        fieldPanel.add(label);
        fieldPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        fieldPanel.add(field);

        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void addFieldToPanel(JPanel panel, String labelText, JComboBox<?> field) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.X_AXIS));
        fieldPanel.setMaximumSize(new Dimension(400, 35));

        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));

        fieldPanel.add(label);
        fieldPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        fieldPanel.add(field);

        panel.add(fieldPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private boolean validateCustomerInput(JTextField nameField, JTextField emailField, JTextField passwordField) {
        if (nameField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty()) {

            DialogUtil.showCustomMessageDialog(
                    this,
                    "Validation Error",
                    "Name, Email, and Password are required fields.",
                    "OK",
                    UIManager.getIcon("OptionPane.errorIcon"));
            return false;
        }
        return true;
    }

    // Method to delete a customer
    private void deleteCustomer() {
        String customerId = DialogUtil.showInputDialog(
                this,
                "Delete Customer",
                "Enter Customer ID to delete:");

        if (customerId != null && !customerId.trim().isEmpty()) {
            if (Validator.isValidCustomerId(customerId) && customerService.deleteCustomer(customerId)) {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Success",
                        "Customer deleted successfully.",
                        "OK",
                        UIManager.getIcon("OptionPane.informationIcon"));
            } else {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Error",
                        "Failed to delete customer.",
                        "OK",
                        UIManager.getIcon("OptionPane.errorIcon"));
            }
        }
    }

    // Method to retrieve a customer by ID
    private void getCustomerById() {
        String customerId = DialogUtil.showInputDialog(
                this,
                "View Customer",
                "Enter Customer ID:");

        if (customerId != null && !customerId.trim().isEmpty()) {
            Optional<Customer> customer = customerService.getCustomerById(customerId);
            if (customer.isPresent()) {
                String details = String.format("<html>" +
                        "Customer ID: %s<br>" +
                        "Name: %s<br>" +
                        "Phone: %s<br>" +
                        "Address: %s<br>" +
                        "Email: %s" +
                        "</html>",
                        customer.get().getId(),
                        customer.get().getName(),
                        customer.get().getPhone(),
                        customer.get().getAddress(),
                        customer.get().getEmail());

                DialogUtil.showCustomMessageDialog(
                        this,
                        "Customer Details",
                        details,
                        "OK",
                        UIManager.getIcon("OptionPane.informationIcon"));
            } else {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Not Found",
                        "Customer not found.",
                        "OK",
                        UIManager.getIcon("OptionPane.warningIcon"));
            }
        }
    }

    // Method to retrieve all customers
    private void getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            DialogUtil.showCustomMessageDialog(
                    this,
                    "No Data",
                    "No customers found.",
                    "OK",
                    UIManager.getIcon("OptionPane.informationIcon"));
            return;
        }

        String[] columnNames = { "Customer ID", "Name", "Phone", "Address", "Email" };
        Object[][] data = new Object[customers.size()][5];
        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            data[i] = new Object[] {
                    customer.getId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getAddress(),
                    customer.getEmail()
            };
        }

        showTableDialog("Customer List", columnNames, data);
    }

    // Method to create a new account
    private void createAccount() {
        JDialog dialog = new JDialog(this, "Create Account", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField customerIdField = createStyledField("Customer ID");
        JComboBox<String> accountTypeCombo = new JComboBox<>(
                new String[] { "Savings", "Fixed Deposit" });
        accountTypeCombo.setMaximumSize(new Dimension(300, 30));
        JTextField balanceField = createStyledField("Initial Balance");

        addFieldToPanel(panel, "Customer ID:", customerIdField);
        addFieldToPanel(panel, "Account Type:", accountTypeCombo);
        addFieldToPanel(panel, "Initial Balance:", balanceField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton createButton = new JButton("Create Account");
        JButton cancelButton = new JButton("Cancel");

        styleButton(createButton);
        styleButton(cancelButton);

        createButton.addActionListener(e -> {
            try {
                String customerId = customerIdField.getText().trim();
                String accountTypeStr = (String) accountTypeCombo.getSelectedItem();
                double initialBalance = Double.parseDouble(balanceField.getText().trim());

                Account.AccountType accountType = Account.AccountType.valueOf(
                        accountTypeStr.toUpperCase().replace(" ", "_"));

                Account account = new Account(customerId, initialBalance, accountType);

                if (accountService.saveAccount(account)) {
                    dialog.dispose();
                    DialogUtil.showCustomMessageDialog(
                            this,
                            "Success",
                            "Account created successfully.\nAccount ID: " + account.getId(),
                            "OK",
                            UIManager.getIcon("OptionPane.informationIcon"));
                } else {
                    DialogUtil.showCustomMessageDialog(
                            this,
                            "Error",
                            "Failed to create account.",
                            "OK",
                            UIManager.getIcon("OptionPane.errorIcon"));
                }
            } catch (NumberFormatException ex) {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Error",
                        "Please enter a valid numeric amount.",
                        "OK",
                        UIManager.getIcon("OptionPane.errorIcon"));
            } catch (Exception ex) {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Error",
                        "Error: " + ex.getMessage(),
                        "OK",
                        UIManager.getIcon("OptionPane.errorIcon"));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Method to delete an account
    private void deleteAccount() {
        String accountId = DialogUtil.showInputDialog(
                this,
                "Delete Account",
                "Enter Account ID to delete:");

        if (accountId != null && !accountId.trim().isEmpty()) {
            if (accountService.deleteAccount(accountId)) {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Success",
                        "Account deleted successfully.",
                        "OK",
                        UIManager.getIcon("OptionPane.informationIcon"));
            } else {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Error",
                        "Failed to delete account.",
                        "OK",
                        UIManager.getIcon("OptionPane.errorIcon"));
            }
        }
    }

    // Method to retrieve accounts by customer ID
    private void getAccountsByCustomerId() {
        String customerId = DialogUtil.showInputDialog(
                this,
                "View Accounts",
                "Enter Customer ID:");

        if (customerId != null && !customerId.trim().isEmpty()) {
            List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
            if (accounts.isEmpty()) {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "No Data",
                        "No accounts found for this customer.",
                        "OK",
                        UIManager.getIcon("OptionPane.warningIcon"));
                return;
            }

            String[] columnNames = { "Account ID", "Customer ID", "Balance", "Account Type" };
            Object[][] data = new Object[accounts.size()][4];
            for (int i = 0; i < accounts.size(); i++) {
                Account account = accounts.get(i);
                data[i] = new Object[] {
                        account.getId(),
                        account.getCustomerId(),
                        String.format("$%.2f", account.getBalance()),
                        account.getAccountType()
                };
            }

            showTableDialog("Accounts for Customer: " + customerId, columnNames, data);
        }
    }

    // Method to view transaction history for a customer
    private void viewTransactionHistory() {
        String customerId = DialogUtil.showInputDialog(
                this,
                "View Transactions",
                "Enter Customer ID:");

        if (customerId != null && !customerId.trim().isEmpty()) {
            List<Transaction> transactions = transactionService.getTransactionsByCustomerId(customerId);
            if (transactions.isEmpty()) {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "No Data",
                        "No transactions found for this customer.",
                        "OK",
                        UIManager.getIcon("OptionPane.warningIcon"));
                return;
            }

            String[] columnNames = { "Transaction ID", "Account ID", "Amount", "Type", "Timestamp" };
            Object[][] data = new Object[transactions.size()][5];
            for (int i = 0; i < transactions.size(); i++) {
                Transaction transaction = transactions.get(i);
                data[i] = new Object[] {
                        transaction.getId(),
                        transaction.getAccountId(),
                        String.format("$%.2f", transaction.getAmount()),
                        transaction.getTransactionType(),
                        transaction.getTimestamp()
                };
            }

            showTableDialog("Transaction History for Customer: " + customerId, columnNames, data);
        }
    }

    // Method to retrieve all transactions
    private void viewAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        if (transactions.isEmpty()) {
            DialogUtil.showCustomMessageDialog(
                    this,
                    "No Data",
                    "No transactions found in the system.",
                    "OK",
                    UIManager.getIcon("OptionPane.warningIcon"));
            return;
        }

        String[] columnNames = { "Transaction ID", "Account ID", "Amount", "Type", "Timestamp" };
        Object[][] data = new Object[transactions.size()][5];
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            data[i] = new Object[] {
                    transaction.getId(),
                    transaction.getAccountId(),
                    String.format("$%.2f", transaction.getAmount()),
                    transaction.getTransactionType(),
                    transaction.getTimestamp()
            };
        }

        showTableDialog("All Transactions", columnNames, data);
    }

    private void showTableDialog(String title, String[] columnNames, Object[][] data) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(this);

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Style the table
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton closeButton = new JButton("Close");
        styleButton(closeButton);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // Helper method to style buttons consistently
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        // Add hover animation
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                AnimationUtil.fadeButtonColor(button, button.getBackground(), button.getBackground().darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                AnimationUtil.fadeButtonColor(button, button.getBackground(), PRIMARY_COLOR);
            }
        });
    }

    // Add these methods
    private void addDarkModeToggle() {
        JButton toggleDarkModeButton = new JButton("Toggle Dark Mode");
        styleButton(toggleDarkModeButton);
        toggleDarkModeButton.addActionListener(e -> toggleDarkMode());
        toggleDarkModeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(toggleDarkModeButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled;
        try {
            UIManager.setLookAndFeel(darkModeEnabled ? new FlatDarkLaf() : new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}