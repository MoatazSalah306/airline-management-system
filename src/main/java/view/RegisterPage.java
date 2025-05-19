package view;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import model.Role;
import model.User;

/**
 * Registration page for the Flight Booking System
 */
public class RegisterPage extends JFrame {
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JSpinner ageSpinner;
    private JComboBox<Role> roleField;  // Changed to use Role objects instead of String


    public RegisterPage() {
        setTitle("SkyJourney Airlines - Register");
        setSize(500, 500);
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
        JLabel titleLabel = new JLabel("Create Your Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        // Username
        JPanel usernamePanel = createFieldPanel("Username:", usernameField = new JTextField(20));
        
        // Email
        JPanel emailPanel = createFieldPanel("Email:", emailField = new JTextField(20));
        
        // Phone
        JPanel phonePanel = createFieldPanel("Phone:", phoneField = new JTextField(20));
        
        // Password
        JPanel passwordPanel = createFieldPanel("Password:", passwordField = new JPasswordField(20));
        
        // Confirm Password
        JPanel confirmPanel = createFieldPanel("Confirm:", confirmPasswordField = new JPasswordField(20));

        // User role - now loading from database
        roleField = new JComboBox<>();
        loadRolesFromDatabase();
        JPanel rolePanel = createFieldPanel("Role:", roleField);

        // Age
        JPanel agePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        agePanel.setOpaque(false);
        JLabel ageLabel = new JLabel("Age:");
        ageLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ageLabel.setForeground(Color.WHITE);
        ageLabel.setPreferredSize(new Dimension(100, 25));
        
        SpinnerNumberModel ageModel = new SpinnerNumberModel(30, 18, 120, 1);
        ageSpinner = new JSpinner(ageModel);
        ageSpinner.setPreferredSize(new Dimension(70, 30));
        ageSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        
        agePanel.add(ageLabel);
        agePanel.add(ageSpinner);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton registerButton = new JButton("Register");
        styleButton(registerButton, new Color(46, 139, 87)); // Sea green
        registerButton.addActionListener(_ -> handleRegistration());

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(178, 34, 34)); // Firebrick red
        cancelButton.addActionListener(_ -> {
            dispose();
            new LoginPage();
        });

        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);

        // Add components to form panel
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(usernamePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(emailPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(phonePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(passwordPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(confirmPanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(rolePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(agePanel);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        formPanel.add(buttonPanel);

        panel.add(formPanel, BorderLayout.CENTER);
        
        // Add decorative footer
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2025 SkyJourney Airlines - Secure Registration");
        footerLabel.setForeground(new Color(200, 200, 200));
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        footerPanel.add(footerLabel);
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        add(panel);
        setVisible(true);
    }
    
    /**
     * Loads all roles from the database into the role dropdown
     */
    private void loadRolesFromDatabase() {
        try {
            // Get all roles from the database
            java.util.List<Role> roles = Role.loadAll();
            
            // Clear existing items
            roleField.removeAllItems();
            
            // Add roles to the combobox
            for (Role role : roles) {
                roleField.addItem(role);
            }
            
            // If there are roles, select the first one by default
            if (roleField.getItemCount() > 0) {
                roleField.setSelectedIndex(0);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Failed to load roles: " + ex.getMessage());
            
            // Add some default roles as fallback
            roleField.removeAllItems();
            try {
                Role adminRole = new Role("Admin");
                adminRole.setId(1); // Setting a temporary ID since we're just using this as fallback
                roleField.addItem(adminRole);
                
                Role userRole = new Role("User");
                userRole.setId(2); // Setting a temporary ID since we're just using this as fallback
                roleField.addItem(userRole);
            } catch (Exception e) {
                // Ignore any errors here as this is just a fallback
            }
        }
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setPreferredSize(new Dimension(100, 25));
        field.setFont(new Font("Arial", Font.PLAIN, 14));

        if (field instanceof JTextField || field instanceof JPasswordField || field instanceof JComboBox) {
            field.setPreferredSize(new Dimension(250, 30));
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
        button.setPreferredSize(new Dimension(120, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void handleRegistration() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        Role selectedRole = (Role) roleField.getSelectedItem();
        int age = (Integer) ageSpinner.getValue();

        // Validate inputs
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            showError("All fields are required");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("Invalid email format");
            return;
        }

        try {
            // Check if email already exists
            try {
                User existingUser = User.loadWithEmail(email);
                if (existingUser != null) {
                    showError("Email already registered");
                    return;
                }
            } catch (SQLException ex) {
                // No user found with this email, which is what we want
            }

            if (selectedRole == null) {
                showError("Please select a role");
                return;
            }

            // Create new user with the selected role
            User newUser = new User(username, email, phone, password, age, selectedRole);
            newUser.register();

            JOptionPane.showMessageDialog(this,
                "Registration successful! Please log in with your new account.",
                "Registration Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Return to login page
            dispose();
            new LoginPage();

        } catch (SQLException ex) {
            ex.printStackTrace();
            showError("Registration failed: " + ex.getMessage());
        }
    }
  
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, 
            message, 
            "Registration Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}