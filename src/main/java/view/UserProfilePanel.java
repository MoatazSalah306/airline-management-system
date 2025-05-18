package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import model.*;

/**
 * User profile panel for viewing and editing user information
 */
public class UserProfilePanel extends JPanel {
    private FlightBookingApp parent;
    private User currentUser;
    
    // Form fields
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JSpinner ageSpinner;
    
    public UserProfilePanel(FlightBookingApp app) {
        this.parent = app;
        this.currentUser = app.getCurrentUser();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("My Profile");
        headerLabel.setFont(FlightBookingApp.HEADER_FONT);
        add(headerLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // User info section
        JPanel userInfoPanel = createUserInfoPanel();
        contentPanel.add(userInfoPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Password change section
        JPanel passwordPanel = createPasswordPanel();
        contentPanel.add(passwordPanel);
        
        // Add content to scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlPanel.setBackground(Color.WHITE);
        
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> parent.navigateTo("search"));
        
        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(FlightBookingApp.PRIMARY_COLOR);
        saveButton.setForeground(Color.BLACK);
        saveButton.addActionListener(e -> saveUserData());
        
        controlPanel.add(backButton);
        controlPanel.add(saveButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        usernameField = new JTextField(20);
        panel.add(usernameField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        emailField = new JTextField(20);
        panel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Phone:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        phoneField = new JTextField(20);
        panel.add(phoneField, gbc);
        
        // Age
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Age:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        SpinnerNumberModel ageModel = new SpinnerNumberModel(30, 18, 120, 1);
        ageSpinner = new JSpinner(ageModel);
        panel.add(ageSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Change Password"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Current Password
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Current Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        currentPasswordField = new JPasswordField(20);
        panel.add(currentPasswordField, gbc);
        
        // New Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("New Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        newPasswordField = new JPasswordField(20);
        panel.add(newPasswordField, gbc);
        
        // Confirm Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        confirmPasswordField = new JPasswordField(20);
        panel.add(confirmPasswordField, gbc);
        
        return panel;
    }
    
    public void loadUserData() {
        // Load current user data into form fields
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());
        ageSpinner.setValue(currentUser.getAge());
        
        // Clear password fields
        currentPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }
    
    private void saveUserData() {
        try {
            // Update user information
            currentUser.setUsername(usernameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setPhone(phoneField.getText());
            currentUser.setAge((Integer) ageSpinner.getValue());
            
            // Check if password change is requested
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            if (!currentPassword.isEmpty()) {
                // Verify current password
                if (!currentUser.verifyPassword(currentPassword)) {
                    JOptionPane.showMessageDialog(this, 
                        "Current password is incorrect.", 
                        "Password Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Verify new password
                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, 
                        "New password cannot be empty.", 
                        "Password Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Verify password confirmation
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(this, 
                        "New password and confirmation do not match.", 
                        "Password Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update password
                currentUser.resetPassword(newPassword);
            }
            
            // Save changes to database
            currentUser.update(currentUser.getUsername(),currentUser.getEmail(),currentUser.getPhone(),currentUser.getAge());
            
            JOptionPane.showMessageDialog(this, 
                "Profile updated successfully.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
                
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating profile: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
