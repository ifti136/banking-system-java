package com.yourname.banking.view;

import com.yourname.banking.model.Customer;
import com.yourname.banking.model.Account;
import com.yourname.banking.model.Transaction;
import com.yourname.banking.service.CustomerService;
import com.yourname.banking.service.AccountService;
import com.yourname.banking.service.TransactionService;
//import com.yourname.banking.util.Validator;
import com.yourname.banking.util.DialogUtil;
import com.yourname.banking.util.AnimationUtil;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableCellRenderer;
//import javax.swing.table.DefaultCellEditor;
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
        setSize(800, 600);
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
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(7, 1, 10, 10));
        mainPanel.add(buttonPanel);

        // Add buttons with updated options
        addButton(buttonPanel, "Add Customer", e -> addCustomer());
        addButton(buttonPanel, "Get All Customers", e -> getAllCustomers());
        addButton(buttonPanel, "Create Account", e -> createAccount());
        addButton(buttonPanel, "Delete Account", e -> deleteAccount());
        addButton(buttonPanel, "Get Accounts By Customer ID", e -> getAccountsByCustomerId());
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
                                "Customer added successfully.\nCustomer ID: " + customer.getId(),
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

    // Method to retrieve all customers
    private void getAllCustomers() {
        JDialog dialog = new JDialog(this, "Customer List", true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        styleButton(searchButton);
        searchPanel.add(new JLabel("Search by Customer ID: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        dialog.add(searchPanel, BorderLayout.NORTH);

        // Table panel
        String[] columnNames = { "Customer ID", "Name", "Phone", "Address", "Email", "Actions" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

        // Add action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        styleButton(editButton);
        styleButton(deleteButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Load all customers initially
        loadCustomersIntoTable(model, null);

        // Search button action
        searchButton.addActionListener(e -> {
            String searchId = searchField.getText().trim();
            loadCustomersIntoTable(model, searchId);
        });

        // Edit button action
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String customerId = (String) table.getValueAt(selectedRow, 0);
                editCustomer(customerId);
                loadCustomersIntoTable(model, null);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a customer to edit", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Delete button action
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String customerId = (String) table.getValueAt(selectedRow, 0);
                if (deleteCustomer(customerId)) {
                    model.removeRow(selectedRow);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a customer to delete", "No Selection",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void loadCustomersIntoTable(DefaultTableModel model, String searchId) {
        model.setRowCount(0);
        List<Customer> customers;

        if (searchId != null && !searchId.isEmpty()) {
            Optional<Customer> customer = customerService.getCustomerById(searchId);
            customers = customer.map(List::of).orElse(List.of());
        } else {
            customers = customerService.getAllCustomers();
        }

        for (Customer customer : customers) {
            model.addRow(new Object[] {
                    customer.getId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getAddress(),
                    customer.getEmail(),
                    "Actions"
            });
        }
    }

    private void editCustomer(String customerId) {
        Optional<Customer> customerOpt = customerService.getCustomerById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            showEditCustomerDialog(customer);
        }
    }

    private void showEditCustomerDialog(Customer customer) {
        JDialog dialog = new JDialog(this, "Edit Customer", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextField nameField = new JTextField(customer.getName());
        JTextField phoneField = new JTextField(customer.getPhone());
        JTextField addressField = new JTextField(customer.getAddress());
        JTextField emailField = new JTextField(customer.getEmail());

        addFieldToPanel(panel, "Name:", nameField);
        addFieldToPanel(panel, "Phone:", phoneField);
        addFieldToPanel(panel, "Address:", addressField);
        addFieldToPanel(panel, "Email:", emailField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> {
            customer.setName(nameField.getText().trim());
            customer.setPhone(phoneField.getText().trim());
            customer.setAddress(addressField.getText().trim());
            customer.setEmail(emailField.getText().trim());

            if (customerService.updateCustomer(customer)) {
                dialog.dispose();
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Success",
                        "Customer details updated successfully.",
                        "OK",
                        UIManager.getIcon("OptionPane.informationIcon"));
                // Refresh the customer list
                getAllCustomers();
            } else {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Error",
                        "Failed to update customer details.",
                        "OK",
                        UIManager.getIcon("OptionPane.errorIcon"));
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
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

    // Method to view all transactions
    private void viewAllTransactions() {
        JDialog dialog = new JDialog(this, "All Transactions", true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        styleButton(searchButton);
        searchPanel.add(new JLabel("Search by Transaction ID: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        dialog.add(searchPanel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = { "Transaction ID", "Account ID", "Amount", "Type", "Timestamp" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

        // Load all transactions initially
        loadTransactionsIntoTable(model, null);

        // Search button action
        searchButton.addActionListener(e -> {
            String searchId = searchField.getText().trim();
            loadTransactionsIntoTable(model, searchId);
        });

        dialog.add(new JScrollPane(table), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void loadTransactionsIntoTable(DefaultTableModel model, String searchId) {
        model.setRowCount(0);
        if (searchId != null && !searchId.isEmpty()) {
            Transaction transaction = transactionService.getTransactionById(searchId);
            if (transaction != null) {
                model.addRow(new Object[] {
                        transaction.getId(),
                        transaction.getAccountId(),
                        String.format("$%.2f", transaction.getAmount()),
                        transaction.getTransactionType(),
                        transaction.getTimestamp()
                });
            }
        } else {
            List<Transaction> transactions = transactionService.getAllTransactions();
            for (Transaction transaction : transactions) {
                model.addRow(new Object[] {
                        transaction.getId(),
                        transaction.getAccountId(),
                        String.format("$%.2f", transaction.getAmount()),
                        transaction.getTransactionType(),
                        transaction.getTimestamp()
                });
            }
        }
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

    private boolean deleteCustomer(String customerId) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this customer?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (customerService.deleteCustomer(customerId)) {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Success",
                        "Customer deleted successfully.",
                        "OK",
                        UIManager.getIcon("OptionPane.informationIcon"));
                return true;
            } else {
                DialogUtil.showCustomMessageDialog(
                        this,
                        "Error",
                        "Failed to delete customer.",
                        "OK",
                        UIManager.getIcon("OptionPane.errorIcon"));
            }
        }
        return false;
    }
}