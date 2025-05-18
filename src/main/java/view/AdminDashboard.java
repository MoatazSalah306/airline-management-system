package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;
import java.text.SimpleDateFormat;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import model.*;

/**
 * Admin Dashboard for the Flight Booking System
 */
public class AdminDashboard extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // Dashboard panels
    private JPanel dashboardPanel;
    private JPanel userManagementPanel;
    private JPanel flightManagementPanel;
    private JPanel bookingManagementPanel;
    private JPanel systemConfigPanel;
    
    // Tables
    private JTable usersTable;
    private JTable flightsTable;
    private JTable bookingsTable;
    
    // Statistics
    private JLabel totalUsersLabel;
    private JLabel totalFlightsLabel;
    private JLabel totalBookingsLabel;
    private JLabel revenueLabel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        
        // Verify admin access
        if (!user.isAdmin()) {
            JOptionPane.showMessageDialog(null, 
                "Access denied. Admin privileges required.", 
                "Security Error", 
                JOptionPane.ERROR_MESSAGE);
            new LoginPage();
            return;
        }
        
        setTitle("SkyJourney Airlines - Admin Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create sidebar and content area
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setEnabled(false); // Prevent resizing
        
        // Create sidebar
        JPanel sidebarPanel = createSidebar();
        splitPane.setLeftComponent(sidebarPanel);
        
        // Create content panels
        dashboardPanel = createDashboardPanel();
        userManagementPanel = createUserManagementPanel();
        flightManagementPanel = createFlightManagementPanel();
        bookingManagementPanel = createBookingManagementPanel();
        systemConfigPanel = createSystemConfigPanel();
        
        // Add panels to card layout
        mainPanel.add(dashboardPanel, "dashboard");
        mainPanel.add(userManagementPanel, "users");
        mainPanel.add(flightManagementPanel, "flights");
        mainPanel.add(bookingManagementPanel, "bookings");
        mainPanel.add(systemConfigPanel, "config");
        
        splitPane.setRightComponent(mainPanel);
        
        // Show dashboard initially
        cardLayout.show(mainPanel, "dashboard");
        
        add(splitPane);
        setVisible(true);
        
        // Load initial data
        loadDashboardData();
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(51, 51, 51)); // Dark gray
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        // Admin info panel
        JPanel adminPanel = new JPanel();
        adminPanel.setLayout(new BoxLayout(adminPanel, BoxLayout.Y_AXIS));
        adminPanel.setBackground(new Color(51, 51, 51));
        adminPanel.setMaximumSize(new Dimension(180, 100));
        adminPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        JLabel adminName = new JLabel(currentUser.getUsername());
        adminName.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminName.setForeground(Color.WHITE);
        adminName.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel adminRole = new JLabel("Administrator");
        adminRole.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminRole.setForeground(new Color(180, 180, 180));
        adminRole.setFont(new Font("Arial", Font.ITALIC, 12));
        
        adminPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        adminPanel.add(adminName);
        adminPanel.add(adminRole);
        
        sidebar.add(adminPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Navigation buttons
        String[] navItems = {"Dashboard", "User Management", "Flight Management", "Booking Management", "System Config"};
        String[] navTargets = {"dashboard", "users", "flights", "bookings", "config"};
        
        for (int i = 0; i < navItems.length; i++) {
            JButton navButton = createNavButton(navItems[i], navTargets[i]);
            sidebar.add(navButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        // Logout button at bottom
        sidebar.add(Box.createVerticalGlue());
        JButton logoutButton = createNavButton("Logout", "logout");
        logoutButton.setBackground(new Color(178, 34, 34)); // Firebrick red
        sidebar.add(logoutButton);
        
        return sidebar;
    }
    
    private JButton createNavButton(String text, String target) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(180, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBackground(new Color(70, 130, 180)); // Steel blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addActionListener(e -> {
            if ("logout".equals(target)) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginPage();
                }
            } else {
                cardLayout.show(mainPanel, target);
            }
        });
        
        return button;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("Admin Dashboard");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(Color.WHITE);
        
        // Statistics cards
        contentPanel.add(createStatCard("Total Users", "0", new Color(70, 130, 180))); // Steel blue
        contentPanel.add(createStatCard("Total Flights", "0", new Color(46, 139, 87))); // Sea green
        contentPanel.add(createStatCard("Active Bookings", "0", new Color(255, 140, 0))); // Dark orange
        contentPanel.add(createStatCard("Total Revenue", "$0", new Color(106, 90, 205))); // Slate blue
        
        // Center the content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(contentPanel, BorderLayout.NORTH);
        
        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout(0, 10));
        activityPanel.setBackground(Color.WHITE);
        activityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        
        String[] columns = {"Type", "User", "Details", "Date"};
        Object[][] data = {
            {"Login", "admin", "Admin login", "2025-05-17 12:00:00"},
            {"Booking", "john_doe", "Flight JFK-LAX", "2025-05-16 15:30:00"},
            {"Payment", "jane_smith", "$450.00 received", "2025-05-16 14:15:00"},
            {"Registration", "new_user", "New user registered", "2025-05-16 10:20:00"}
        };
        
        JTable activityTable = new JTable(new DefaultTableModel(data, columns));
        activityTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(activityTable);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        
        activityPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(activityPanel, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer with refresh button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> loadDashboardData());
        footerPanel.add(refreshButton);
        
        panel.add(footerPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        // Store reference to update later
        if (title.contains("Users")) {
            totalUsersLabel = valueLabel;
        } else if (title.contains("Flights")) {
            totalFlightsLabel = valueLabel;
        } else if (title.contains("Bookings")) {
            totalBookingsLabel = valueLabel;
        } else if (title.contains("Revenue")) {
            revenueLabel = valueLabel;
        }
        
        return card;
    }
    
    private JPanel createUserManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("User Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // User table
        String[] columns = {"ID", "Username", "Email", "Role", "Created Date", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column is editable
            }
        };
        
        usersTable = new JTable(model);
        usersTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        
        JButton addUserButton = new JButton("Add User");
        addUserButton.addActionListener(e -> showAddUserDialog());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadUsers());
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchUsers(searchField.getText()));
        
        controlPanel.add(addUserButton);
        controlPanel.add(refreshButton);
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // Load users when panel is created
        loadUsers();
        
        return panel;
    }
    
    private JPanel createFlightManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("Flight Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Flight table
        String[] columns = {"ID", "From", "To", "Aircraft", "Schedule", "Duration", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };
        
        flightsTable = new JTable(model);
        flightsTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(flightsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        
        JButton addFlightButton = new JButton("Add Flight");
        addFlightButton.addActionListener(e -> showAddFlightDialog());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadFlights());
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchFlights(searchField.getText()));
        
        controlPanel.add(addFlightButton);
        controlPanel.add(refreshButton);
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // Load flights when panel is created
        loadFlights();
        
        return panel;
    }
    
    private JPanel createBookingManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("Booking Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Booking table
        String[] columns = {"ID", "User", "Flight", "Date", "Status", "Payment", "Actions"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };
        
        bookingsTable = new JTable(model);
        bookingsTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadBookings());
        
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchBookings(searchField.getText()));
        
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All Statuses", "Confirmed", "Pending", "Canceled"});
        statusFilter.addActionListener(e -> filterBookingsByStatus((String)statusFilter.getSelectedItem()));
        
        controlPanel.add(refreshButton);
        controlPanel.add(new JLabel("Status:"));
        controlPanel.add(statusFilter);
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // Load bookings when panel is created
        loadBookings();
        
        return panel;
    }
    
    private JPanel createSystemConfigPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("System Configuration");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Config tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Airports tab
        JPanel airportsPanel = new JPanel(new BorderLayout());
        String[] airportColumns = {"ID", "Code", "Name", "Country", "Status", "Actions"};
        JTable airportsTable = new JTable(new DefaultTableModel(airportColumns, 0));
        airportsTable.setFillsViewportHeight(true);
        JScrollPane airportsScroll = new JScrollPane(airportsTable);
        
        JPanel airportControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        airportControls.add(new JButton("Add Airport"));
        airportControls.add(new JButton("Refresh"));
        
        airportsPanel.add(airportsScroll, BorderLayout.CENTER);
        airportsPanel.add(airportControls, BorderLayout.SOUTH);
        
        // Airlines tab
        JPanel airlinesPanel = new JPanel(new BorderLayout());
        String[] airlineColumns = {"ID", "Code", "Name", "Actions"};
        JTable airlinesTable = new JTable(new DefaultTableModel(airlineColumns, 0));
        airlinesTable.setFillsViewportHeight(true);
        JScrollPane airlinesScroll = new JScrollPane(airlinesTable);
        
        JPanel airlineControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        airlineControls.add(new JButton("Add Airline"));
        airlineControls.add(new JButton("Refresh"));
        
        airlinesPanel.add(airlinesScroll, BorderLayout.CENTER);
        airlinesPanel.add(airlineControls, BorderLayout.SOUTH);
        
        // System settings tab
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Add some example settings
        settingsPanel.add(createSettingRow("Database Backup", new JButton("Backup Now")));
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(createSettingRow("Email Notifications", new JCheckBox("Enabled")));
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsPanel.add(createSettingRow("Maintenance Mode", new JCheckBox("Enabled")));
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JButton saveSettingsButton = new JButton("Save Settings");
        saveSettingsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        settingsPanel.add(saveSettingsButton);
        
        // Add tabs
        tabbedPane.addTab("Airports", airportsPanel);
        tabbedPane.addTab("Airlines", airlinesPanel);
        tabbedPane.addTab("System Settings", settingsPanel);
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSettingRow(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel settingLabel = new JLabel(label + ":");
        settingLabel.setPreferredSize(new Dimension(150, 25));
        
        panel.add(settingLabel);
        panel.add(component);
        
        return panel;
    }
    
    // Data loading methods
    private void loadDashboardData() {
        try {
            // These would normally query the database
            int userCount = 10; // Placeholder
            int flightCount = 15; // Placeholder
            int bookingCount = 25; // Placeholder
            double revenue = 12750.00; // Placeholder
            
            // Update UI
            totalUsersLabel.setText(String.valueOf(userCount));
            totalFlightsLabel.setText(String.valueOf(flightCount));
            totalBookingsLabel.setText(String.valueOf(bookingCount));
            revenueLabel.setText(String.format("$%.2f", revenue));
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading dashboard data: " + e.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadUsers() {
        try {
            // Clear existing data
            DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
            model.setRowCount(0);
            
            // Add sample data (would normally come from database)
            model.addRow(new Object[]{1, "admin", "admin@airport.com", "Admin", "2023-01-01", "Edit/Delete"});
            model.addRow(new Object[]{2, "john_doe", "john.doe@example.com", "Customer", "2023-01-02", "Edit/Delete"});
            model.addRow(new Object[]{3, "jane_smith", "jane.smith@example.com", "Customer", "2023-01-03", "Edit/Delete"});
            model.addRow(new Object[]{4, "airline_staff1", "staff1@airline.com", "Airline Staff", "2023-01-04", "Edit/Delete"});
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading users: " + e.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadFlights() {
        try {
            // Clear existing data
            DefaultTableModel model = (DefaultTableModel) flightsTable.getModel();
            model.setRowCount(0);
            
            // Add sample data (would normally come from database)
            model.addRow(new Object[]{1, "JFK", "LAX", "Boeing 737-800", "Monday 08:00", "360 min", "Edit/Delete"});
            model.addRow(new Object[]{2, "JFK", "LHR", "Boeing 777-300ER", "Monday 14:30", "420 min", "Edit/Delete"});
            model.addRow(new Object[]{3, "LHR", "JFK", "Airbus A380", "Tuesday 09:15", "440 min", "Edit/Delete"});
            model.addRow(new Object[]{4, "LHR", "CDG", "Airbus A380", "Wednesday 11:45", "90 min", "Edit/Delete"});
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading flights: " + e.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadBookings() {
        try {
            // Clear existing data
            DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
            model.setRowCount(0);
            
            // Add sample data (would normally come from database)
            model.addRow(new Object[]{1, "John Doe", "JFK-LAX", "2023-02-15", "Confirmed", "$450.00", "View/Edit"});
            model.addRow(new Object[]{2, "Jane Smith", "JFK-LHR", "2023-02-16", "Confirmed", "$550.00", "View/Edit"});
            model.addRow(new Object[]{3, "Emma Wilson", "LHR-JFK", "2023-02-17", "Confirmed", "$325.00", "View/Edit"});
            model.addRow(new Object[]{4, "Michael Brown", "LHR-CDG", "2023-02-18", "Confirmed", "$750.00", "View/Edit"});
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading bookings: " + e.getMessage(), 
                "Data Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Dialog methods
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        panel.add(createDialogField("Username:", new JTextField(20)));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Email:", new JTextField(20)));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Password:", new JPasswordField(20)));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Phone:", new JTextField(20)));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Age:", new JTextField(20)));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Role selection
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"Customer", "Admin", "Airline Staff", "Airport Staff"});
        panel.add(createDialogField("Role:", roleCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            // Would normally save to database
            JOptionPane.showMessageDialog(dialog, "User added successfully!");
            dialog.dispose();
            loadUsers(); // Refresh user list
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showAddFlightDialog() {
        JDialog dialog = new JDialog(this, "Add New Flight", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JComboBox<String> departureCombo = new JComboBox<>(new String[]{"JFK", "LAX", "LHR", "CDG", "HND"});
        panel.add(createDialogField("Departure Airport:", departureCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JComboBox<String> arrivalCombo = new JComboBox<>(new String[]{"JFK", "LAX", "LHR", "CDG", "HND"});
        panel.add(createDialogField("Arrival Airport:", arrivalCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JComboBox<String> aircraftCombo = new JComboBox<>(new String[]{
            "Boeing 737-800", "Boeing 777-300ER", "Airbus A320", "Boeing 767-300", 
            "Boeing 787-9", "Airbus A380", "Airbus A350-900"
        });
        panel.add(createDialogField("Aircraft:", aircraftCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JComboBox<String> dayCombo = new JComboBox<>(new String[]{
            "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        });
        panel.add(createDialogField("Day:", dayCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panel.add(createDialogField("Departure Time:", new JTextField("08:00")));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panel.add(createDialogField("Duration (minutes):", new JTextField("360")));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        panel.add(createDialogField("Gate:", new JTextField("A1")));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            // Would normally save to database
            JOptionPane.showMessageDialog(dialog, "Flight added successfully!");
            dialog.dispose();
            loadFlights(); // Refresh flight list
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private JPanel createDialogField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        
        panel.add(label);
        panel.add(field);
        
        return panel;
    }
    
    // Search and filter methods
    private void searchUsers(String query) {
        // Would normally search in database
        JOptionPane.showMessageDialog(this, "Searching for users matching: " + query);
        loadUsers(); // For demo, just reload all
    }
    
    private void searchFlights(String query) {
        // Would normally search in database
        JOptionPane.showMessageDialog(this, "Searching for flights matching: " + query);
        loadFlights(); // For demo, just reload all
    }
    
    private void searchBookings(String query) {
        // Would normally search in database
        JOptionPane.showMessageDialog(this, "Searching for bookings matching: " + query);
        loadBookings(); // For demo, just reload all
    }
    
    private void filterBookingsByStatus(String status) {
        // Would normally filter in database
        if ("All Statuses".equals(status)) {
            loadBookings(); // Load all
        } else {
            JOptionPane.showMessageDialog(this, "Filtering bookings by status: " + status);
            // For demo, just reload all
            loadBookings();
        }
    }
}
