package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import model.Role;
import model.User;
import util.SecurityUtil;

/**
 * Login page for the Flight Booking System
 */
public class LoginPage extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckbox;

    public LoginPage() {
        setTitle("SkyJourney Airlines - Login");
        setSize(450, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        JPanel panel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(102, 0, 153);
                Color color2 = new Color(51, 0, 102);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("SkyJourney Airlines", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Email
        JPanel emailPanel = createFieldPanel("Email:", emailField = new JTextField(20));
        
        // Password
        JPanel passwordPanel = createFieldPanel("Password:", passwordField = new JPasswordField(20));
        
        // // Remember me checkbox
        // JPanel rememberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // rememberPanel.setOpaque(false);
        // rememberMeCheckbox = new JCheckBox("Remember me");
        // rememberMeCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        // rememberMeCheckbox.setForeground(Color.WHITE);
        // rememberMeCheckbox.setOpaque(false);
        // rememberPanel.add(rememberMeCheckbox);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton loginButton = new JButton("Login");
        styleButton(loginButton, new Color(46, 139, 87)); // Sea green
        loginButton.addActionListener(e -> handleLogin());

        JButton registerButton = new JButton("Register");
        styleButton(registerButton, new Color(70, 130, 180)); // Steel blue
        registerButton.addActionListener(_ -> {
            dispose();
            new RegisterPage();
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // // Forgot Password
        // JLabel forgotPasswordLabel = new JLabel("Forgot Password?");
        // forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        // forgotPasswordLabel.setForeground(Color.WHITE);
        // forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // forgotPasswordLabel.addMouseListener(new MouseAdapter() {
        //     @Override
        //     public void mouseClicked(MouseEvent e) {
        //         handleForgotPassword();
        //     }
            
        //     @Override
        //     public void mouseEntered(MouseEvent e) {
        //         forgotPasswordLabel.setText("<html><u>Forgot Password?</u></html>");
        //     }
            
        //     @Override
        //     public void mouseExited(MouseEvent e) {
        //         forgotPasswordLabel.setText("Forgot Password?");
        //     }
        // });
        
        // JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        // forgotPanel.setOpaque(false);
        // forgotPanel.add(forgotPasswordLabel);

        // Add components to form panel
        formPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        formPanel.add(emailPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        // formPanel.add(rememberPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonPanel);
        // formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        // formPanel.add(forgotPanel);

        panel.add(formPanel, BorderLayout.CENTER);
        
        // Add decorative footer
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 SkyJourney Airlines - Secure Login");
        footerLabel.setForeground(new Color(200, 200, 200));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerPanel.add(footerLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        add(panel);
        setVisible(true);
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(100, 25));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        if (field instanceof JTextField || field instanceof JPasswordField) {
            field.setPreferredSize(new Dimension(200, 30));
        }
        panel.add(label);
        panel.add(field);
        return panel;
    }
    
    private void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }
        
        try {
            User user = User.loadWithEmail(email);
            
            if (user.verifyPassword(password)) {
                // Check if we need to upgrade legacy password
                if (!user.getPassword().contains(":")) {
                    // Upgrade to secure password
                    user.resetPassword(password);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Login successful! Welcome, " + user.getUsername(), 
                    "Login Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                
                // Check user role and direct to appropriate dashboard
                if (user.isAdmin()) {
                    new AdminDashboard(user).setVisible(true);
                } else {
                    new FlightBookingApp(user).setVisible(true);
                }
            } else {
                showError("Invalid password");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("User not found or database error: " + ex.getMessage());
        }
    }
    
    private void handleForgotPassword() {
        String email = JOptionPane.showInputDialog(this, 
            "Enter your email address to reset your password:", 
            "Password Reset", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (email != null && !email.trim().isEmpty()) {
            try {
                User user = User.loadWithEmail(email.trim());
                // In a real application, this would send an email with reset link
                // For this demo, we'll just reset to a temporary password
                String tempPassword = generateTemporaryPassword();
                user.resetPassword(tempPassword);
                
                JOptionPane.showMessageDialog(this, 
                    "A temporary password has been set: " + tempPassword + "\n" +
                    "Please login with this password and change it immediately.", 
                    "Password Reset", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (SQLException ex) {
                showError("Email not found or database error: " + ex.getMessage());
            }
        }
    }
    
    private String generateTemporaryPassword() {
        // Generate a simple 8-character temporary password
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int index = (int)(chars.length() * Math.random());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Login Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}
