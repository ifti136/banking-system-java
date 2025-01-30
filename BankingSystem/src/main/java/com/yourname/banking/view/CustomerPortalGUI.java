package com.yourname.banking.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.yourname.banking.model.Account;
import com.yourname.banking.model.Transaction;
import com.yourname.banking.service.AccountService;
import com.yourname.banking.service.TransactionService;
import com.yourname.banking.service.CustomerService;
import com.yourname.banking.util.Validator;
import com.yourname.banking.util.DialogUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;

public class CustomerPortalGUI extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    private final CustomerService customerService = new CustomerService();
    private final AccountService accountService = new AccountService();
    private final TransactionService transactionService = new TransactionService();
    private final String customerId;

    private boolean darkModeEnabled = false;

    public CustomerPortalGUI(String customerId) {
        this.customerId = customerId;

        applyLookAndFeel();
        setupFrame();
        JPanel mainPanel = setupMainPanel();
        setupTitle(mainPanel);
        setupButtons(mainPanel);
        addDarkModeToggle();
        setVisible(true);
    }

    private void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            darkModeEnabled = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled;
        applyLookAndFeel();
        SwingUtilities.updateComponentTreeUI(this); // Refresh UI to apply new theme
    }

    private void setupFrame() {
        setTitle("Customer Portal");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null); // Center window
    }

    private JPanel setupMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private void setupTitle(JPanel mainPanel) {
        JLabel titleLabel = new JLabel("Welcome to Customer Portal", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        styleButton(button);
        button.addActionListener(action);
        panel.add(button);
    }

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

    private void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add smooth fade animation for hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                new Thread(() -> fadeButtonColor(button, button.getBackground(), button.getBackground().darker()))
                        .start();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                new Thread(() -> fadeButtonColor(button, button.getBackground(), PRIMARY_COLOR)).start();
            }
        });
    }

    private void fadeButtonColor(JButton button, Color startColor, Color endColor) {
        int steps = 15; // Number of animation steps
        int delay = 15; // Delay between steps (ms)

        for (int i = 1; i <= steps; i++) {
            float ratio = (float) i / steps;
            Color intermediateColor = blendColors(startColor, endColor, ratio);
            SwingUtilities.invokeLater(() -> button.setBackground(intermediateColor));
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private Color blendColors(Color start, Color end, float ratio) {
        int r = (int) (start.getRed() + (end.getRed() - start.getRed()) * ratio);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * ratio);
        int b = (int) (start.getBlue() + (end.getBlue() - start.getBlue()) * ratio);
        return new Color(r, g, b);
    }

    private void setupButtons(JPanel mainPanel) {
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 10, 10)); // 2 columns, dynamic rows
        mainPanel.add(buttonPanel);

        addButton(buttonPanel, "Make Transaction", e -> makeTransaction());
        addButton(buttonPanel, "View Account Details", e -> viewAccountDetails());
        addButton(buttonPanel, "View Transaction By ID", e -> viewTransactionById());
        addButton(buttonPanel, "View Transaction History", e -> viewTransactionsHistory());
        addButton(buttonPanel, "Change Password", e -> changePassword());
        addButton(buttonPanel, "Exit", e -> dispose());
    }

    private void makeTransaction() {
        String transactionType = showTransactionTypeDialog();
        if (transactionType == null) {
            showCustomMessageDialog(
                    "Transaction Cancelled",
                    "Transaction type not selected.",
                    "OK",
                    UIManager.getIcon("OptionPane.warningIcon"));
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter Amount:");
        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (!Validator.isValidTransactionAmount(amount)) {
                showCustomMessageDialog(
                        "Invalid Amount",
                        "Transaction amount is not valid.",
                        "OK",
                        UIManager.getIcon("OptionPane.errorIcon"));
                return;
            }
        } catch (NumberFormatException ex) {
            showCustomMessageDialog(
                    "Invalid Input",
                    "Please enter a valid numeric amount.",
                    "OK",
                    UIManager.getIcon("OptionPane.errorIcon"));
            return;
        }

        Optional<Account> accountOptional = accountService.getAccountsByCustomerId(customerId).stream().findFirst();
        if (accountOptional.isEmpty()) {
            showCustomMessageDialog(
                    "No Account Found",
                    "No account exists for this customer.",
                    "OK",
                    UIManager.getIcon("OptionPane.errorIcon"));
            return;
        }

        Account account = accountOptional.get();
        if (transactionType.equals("Withdraw") && !Validator.hasSufficientBalance(account, amount)) {
            showCustomMessageDialog(
                    "Insufficient Balance",
                    "You do not have enough balance for this withdrawal.",
                    "OK",
                    UIManager.getIcon("OptionPane.warningIcon"));
            return;
        }

        Transaction transaction = new Transaction(account.getId(), amount, transactionType);
        if (transactionService.saveTransaction(transaction)) {
            if (transactionType.equals("Deposit")) {
                accountService.updateAccountBalance(account.getId(), account.getBalance() + amount);
            } else {
                accountService.updateAccountBalance(account.getId(), account.getBalance() - amount);
            }
            showCustomMessageDialog(
                    "Transaction Successful",
                    "Transaction completed successfully. Transaction ID: " + transaction.getId(),
                    "OK",
                    UIManager.getIcon("OptionPane.informationIcon"));
        } else {
            showCustomMessageDialog(
                    "Transaction Failed",
                    "Something went wrong. Please try again later.",
                    "OK",
                    UIManager.getIcon("OptionPane.errorIcon"));
        }
    }

    private String showTransactionTypeDialog() {
        JDialog dialog = new JDialog(this, "Make Transaction", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(contentPanel, BorderLayout.CENTER);

        JLabel label = new JLabel("Select Transaction Type:", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(label);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        styleDialogButton(depositButton);
        styleDialogButton(withdrawButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        contentPanel.add(buttonPanel);

        JButton cancelButton = new JButton("Cancel");
        styleDialogButton(cancelButton);
        cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(cancelButton);

        final String[] selectedTransaction = { null };
        depositButton.addActionListener(e -> {
            selectedTransaction[0] = "Deposit";
            dialog.dispose();
        });
        withdrawButton.addActionListener(e -> {
            selectedTransaction[0] = "Withdraw";
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
        return selectedTransaction[0];
    }

    private void styleDialogButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    private void showCustomMessageDialog(String title, String message, String buttonText, Icon icon) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(contentPanel, BorderLayout.CENTER);

        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(iconLabel);
            contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>",
                JLabel.CENTER);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(messageLabel);

        JButton closeButton = new JButton(buttonText);
        styleDialogButton(closeButton);
        closeButton.addActionListener(e -> dialog.dispose());
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(closeButton);

        dialog.setVisible(true);
    }

    private void viewAccountDetails() {
        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        if (accounts.isEmpty()) {
            DialogUtil.showCustomMessageDialog(
                    this,
                    "No Account Found",
                    "No account exists for this customer.",
                    "OK",
                    UIManager.getIcon("OptionPane.warningIcon"));
            return;
        }

        StringBuilder accountDetails = new StringBuilder("<html>");
        for (Account account : accounts) {
            accountDetails.append("Account ID: ").append(account.getId())
                    .append("<br>Balance: $").append(String.format("%.2f", account.getBalance()))
                    .append("<br><br>");
        }
        accountDetails.append("</html>");

        DialogUtil.showCustomMessageDialog(
                this,
                "Account Details",
                accountDetails.toString(),
                "OK",
                UIManager.getIcon("OptionPane.informationIcon"));
    }

    private void viewTransactionById() {
        String transactionId = DialogUtil.showInputDialog(
                this,
                "View Transaction",
                "Enter Transaction ID:");

        if (!Validator.isValidTransactionId(transactionId)) {
            DialogUtil.showCustomMessageDialog(
                    this,
                    "Invalid Input",
                    "Invalid Transaction ID format.",
                    "OK",
                    UIManager.getIcon("OptionPane.errorIcon"));
            return;
        }

        Transaction transaction = transactionService.getTransactionById(transactionId);
        if (transaction == null) {
            DialogUtil.showCustomMessageDialog(
                    this,
                    "Not Found",
                    "Transaction not found.",
                    "OK",
                    UIManager.getIcon("OptionPane.warningIcon"));
        } else {
            String details = String.format("<html>" +
                    "Transaction ID: %s<br>" +
                    "Account ID: %s<br>" +
                    "Amount: $%.2f<br>" +
                    "Type: %s<br>" +
                    "Timestamp: %s" +
                    "</html>",
                    transaction.getId(),
                    transaction.getAccountId(),
                    transaction.getAmount(),
                    transaction.getTransactionType(),
                    transaction.getTimestamp());

            DialogUtil.showCustomMessageDialog(
                    this,
                    "Transaction Details",
                    details,
                    "OK",
                    UIManager.getIcon("OptionPane.informationIcon"));
        }
    }

    private void viewTransactionsHistory() {
        List<Account> accounts = accountService.getAccountsByCustomerId(customerId);
        if (accounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No account found for this customer.");
            return;
        }

        StringBuilder transactionHistory = new StringBuilder("Transaction History:\n");

        for (Account account : accounts) {
            List<Transaction> transactions = transactionService.getTransactionsByAccountId(account.getId());
            for (Transaction transaction : transactions) {
                transactionHistory.append("ID: ").append(transaction.getId())
                        .append(", Type: ").append(transaction.getTransactionType())
                        .append(", Amount: ").append(transaction.getAmount())
                        .append(", Date: ").append(transaction.getTimestamp())
                        .append("\n");
            }
        }

        if (transactionHistory.length() == 0) {
            transactionHistory.append("No transactions found.");
        }

        JOptionPane.showMessageDialog(this, transactionHistory.toString(), "Transaction History",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void changePassword() {
        String newPassword = JOptionPane.showInputDialog(this, "Enter New Password:");
        if (newPassword == null || newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty.");
            return;
        }

        if (Validator.isValidPassword(newPassword)) {
            customerService.updateCustomerPassword(customerId, newPassword);
            JOptionPane.showMessageDialog(this, "Password updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Password update failed.");
        }
    }
}