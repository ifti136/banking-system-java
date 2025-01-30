package com.yourname.banking.util;

import java.awt.*;
import javax.swing.*;

public class DialogUtil {
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Font DIALOG_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static void showCustomMessageDialog(JFrame parent, String title, String message, String buttonText, Icon icon) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(parent);
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

        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>" + message + "</div></html>", JLabel.CENTER);
        messageLabel.setFont(DIALOG_FONT);
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

    public static String showInputDialog(JFrame parent, String title, String message) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(contentPanel, BorderLayout.CENTER);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(DIALOG_FONT);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextField inputField = new JTextField(20);
        inputField.setMaximumSize(new Dimension(300, 30));
        inputField.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(inputField);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        styleDialogButton(okButton);
        styleDialogButton(cancelButton);

        final String[] result = {null};
        okButton.addActionListener(e -> {
            result[0] = inputField.getText();
            dialog.dispose();
        });
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(buttonPanel);

        dialog.setVisible(true);
        return result[0];
    }

    private static void styleDialogButton(JButton button) {
        button.setFont(DIALOG_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                AnimationUtil.fadeButtonColor(button, button.getBackground(), button.getBackground().darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                AnimationUtil.fadeButtonColor(button, button.getBackground(), PRIMARY_COLOR);
            }
        });
    }
} 