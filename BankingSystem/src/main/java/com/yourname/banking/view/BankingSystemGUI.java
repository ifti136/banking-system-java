package com.yourname.banking.view;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.yourname.banking.dao.DBConnection;
import com.yourname.banking.service.CustomerService;
import com.yourname.banking.util.AnimationUtil;
import com.yourname.banking.util.DialogUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BankingSystemGUI extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Logger LOGGER = Logger.getLogger(BankingSystemGUI.class.getName());

    private boolean darkModeEnabled = false;

    public BankingSystemGUI() {
        applyLookAndFeel();
        setupFrame();
        JPanel mainPanel = setupMainPanel();
        setupTitleLabel(mainPanel);
        setupButtons(mainPanel);
        addDarkModeToggle();
        setVisible(true);
    }

    private void applyLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            darkModeEnabled = true;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Failed to initialize FlatLaf", ex);
        }
    }

    private void setupFrame() {
        setTitle("Welcome to the Bank");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null); // Center the frame
    }

    private JPanel setupMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private void setupTitleLabel(JPanel mainPanel) {
        JLabel titleLabel = new JLabel("Welcome to the Bank", JLabel.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer
    }

    private void setupButtons(JPanel mainPanel) {
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10)); // Button layout
        mainPanel.add(buttonPanel);

        addButton(buttonPanel, "Customer Portal", e -> openCustomerPortal());
        addButton(buttonPanel, "Admin Panel", e -> authenticateAdminPanel());
        addButton(buttonPanel, "Exit", e -> System.exit(0));
    }

    private void addButton(JPanel panel, String text, ActionListener action) {
        JButton button = new JButton(text);
        styleButton(button);
        button.addActionListener(action);
        panel.add(button);
    }

    private void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

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

    private void authenticateAdminPanel() {
        JDialog dialog = new JDialog(this, "Admin Login", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Enter Admin Password:");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Login");
        JButton cancelButton = new JButton("Cancel");
        
        styleButton(loginButton);
        styleButton(cancelButton);
        
        loginButton.addActionListener(e -> {
            String password = new String(passwordField.getPassword());
            if ("admin123".equals(password)) {
                dialog.dispose();
                try {
                    new AdminPanelGUI().setVisible(true);
                } catch (Exception ex) {
                    showErrorDialog("Error opening admin panel. Please try again.");
                }
            } else {
                showErrorDialog("Incorrect password. Access denied.");
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        panel.add(label);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(passwordField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openCustomerPortal() {
        JTextField customerIdField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] fields = { "Customer ID:", customerIdField, "Password:", passwordField };
        int option = JOptionPane.showConfirmDialog(this, fields, "Customer Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String customerId = customerIdField.getText().trim();
            String password = new String(passwordField.getPassword());
            CustomerService customerService = new CustomerService();

            if (customerService.verifyCustomerCredentials(customerId, password)) {
                new CustomerPortalGUI(customerId).setVisible(true);
            } else {
                showErrorDialog("Invalid Customer ID or Password. Please try again.");
            }
        }
    }

    private void showErrorDialog(String message) {
        DialogUtil.showCustomMessageDialog(
            this,
            "Error",
            message,
            "OK",
            UIManager.getIcon("OptionPane.errorIcon")
        );
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

    private void toggleDarkMode() {
        darkModeEnabled = !darkModeEnabled;
        try {
            UIManager.setLookAndFeel(darkModeEnabled ? new FlatDarkLaf() : new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (!DBConnection.testConnection()) {
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to the database. Exiting application.",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        SwingUtilities.invokeLater(BankingSystemGUI::new);
    }
}