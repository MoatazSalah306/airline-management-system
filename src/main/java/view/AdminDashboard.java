package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.*;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.PlainDocument;
import javax.swing.border.EmptyBorder;
import model.*;
import util.DbConnection;

public class AdminDashboard extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Dashboard panels
    private JPanel dashboardPanel;
    private JPanel userManagementPanel;
    private JPanel flightManagementPanel;
    private JPanel systemConfigPanel;
    private JPanel airlineManagementPanel;


    // Tables
    private JTable usersTable;
    private JTable flightsTable;

    

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
        systemConfigPanel = createSystemConfigPanel();
        airlineManagementPanel = createAirlineManagementPanel();

        // Add panels to card layout
        mainPanel.add(dashboardPanel, "dashboard");
        mainPanel.add(userManagementPanel, "users");
        mainPanel.add(flightManagementPanel, "flights");
        mainPanel.add(systemConfigPanel, "config");
        mainPanel.add(airlineManagementPanel, "airlines");

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
        String[] navItems = { "Dashboard", "User Management", "Flight Management",
                "Airline Management", "System Config" };
        String[] navTargets = { "dashboard", "users", "flights", "airlines", "config" };
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
                        JOptionPane.YES_NO_OPTION);

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

    private Map<String, String> getDashboardStats() {
        Map<String, String> stats = new HashMap<>();
        try (Connection conn = DbConnection.getInstance()) {
            // Total Users
            String userQuery = "SELECT COUNT(*) AS total FROM user";
            try (PreparedStatement stmt = conn.prepareStatement(userQuery);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("users", String.valueOf(rs.getInt("total")));
                }
            }

            // Total Flights
            String flightQuery = "SELECT COUNT(*) AS total FROM flight";
            try (PreparedStatement stmt = conn.prepareStatement(flightQuery);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("flights", String.valueOf(rs.getInt("total")));
                }
            }

            // Total Revenue
            String revenueQuery = "SELECT SUM(payment_amount) AS total FROM payment";
            try (PreparedStatement stmt = conn.prepareStatement(revenueQuery);
                    ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double revenue = rs.getDouble("total");
                    stats.put("revenue", "$" + String.format("%.2f", revenue));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error fetching dashboard stats: " + e.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        return stats;
    }

    private JPanel createDashboardPanel() {

        dashboardPanel = new JPanel(new BorderLayout(10, 10));
JPanel panel = dashboardPanel;

        
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Admin Dashboard");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Main content
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(Color.WHITE);

        Map<String, String> stats = getDashboardStats();
        String totalUsers = stats.getOrDefault("users", "0");
        String totalFlights = stats.getOrDefault("flights", "0");
        String totalRevenue = stats.getOrDefault("revenue", "$0");

        contentPanel.add(createStatCard("Total Users", totalUsers, new Color(70, 130, 180))); // Steel blue
        contentPanel.add(createStatCard("Total Flights", totalFlights, new Color(46, 139, 87))); // Sea green
        contentPanel.add(createStatCard("Total Revenue", totalRevenue, new Color(106, 90, 205))); // Slate blue

        // Center the content
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(contentPanel, BorderLayout.NORTH);

        

        panel.add(centerPanel, BorderLayout.CENTER);

        // Footer with refresh button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);

        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(_ -> loadDashboardData());
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
        String[] columns = { "ID", "Username", "Email", "Phone", "Age", "Role", "Creation Date" };
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
        addUserButton.addActionListener(_ -> showAddUserDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(_ -> loadUsers());

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(_ -> searchUsers(searchField.getText()));

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
        String[] columns = { "ID", "From", "To", "Gate", "Aircraft", "Schedule", "Duration" };
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
        addFlightButton.addActionListener(_ -> showAddFlightDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(_ -> loadFlights());

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(_ -> searchFlights(searchField.getText()));

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

        // Airports tab - Enhanced with full functionality
        JPanel airportsPanel = createAirportsPanel();

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
        tabbedPane.addTab("System Settings", settingsPanel);

        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    // 2. Create a dedicated panel for airport management
    private JPanel createAirportsPanel() {
        JPanel airportsPanel = new JPanel(new BorderLayout(10, 10));
        airportsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        airportsPanel.setBackground(Color.WHITE);

        // Create table model with non-editable cells
        String[] airportColumns = { "ID", "Code", "Name", "Country", "Address", "Status", "Actions" };
        DefaultTableModel airportsModel = new DefaultTableModel(airportColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
            }
        };

        JTable airportsTable = new JTable(airportsModel);
        airportsTable.setFillsViewportHeight(true);

        // Add button renderer for actions column
        airportsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        airportsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), this, "airport"));

        // Set column widths
        airportsTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        airportsTable.getColumnModel().getColumn(1).setPreferredWidth(70); // Code
        airportsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Name
        airportsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Country
        airportsTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Address
        airportsTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Status
        airportsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Actions

        JScrollPane airportsScroll = new JScrollPane(airportsTable);
        airportsPanel.add(airportsScroll, BorderLayout.CENTER);

        // Control panel
        JPanel airportControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        airportControls.setBackground(Color.WHITE);

        JButton addAirportButton = new JButton("Add Airport");
        addAirportButton.addActionListener(e -> showAddAirportDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadAirports());

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchAirports(searchField.getText()));

        JComboBox<String> statusFilter = new JComboBox<>(
                new String[] { "All Statuses", "Active", "Inactive", "Under Maintenance" });
        statusFilter.addActionListener(e -> filterAirportsByStatus((String) statusFilter.getSelectedItem()));

        airportControls.add(addAirportButton);
        airportControls.add(refreshButton);
        airportControls.add(new JLabel("Status:"));
        airportControls.add(statusFilter);
        airportControls.add(new JLabel("Search:"));
        airportControls.add(searchField);
        airportControls.add(searchButton);

        airportsPanel.add(airportControls, BorderLayout.SOUTH);

        // Load airports when panel is created
        loadAirports();

        return airportsPanel;
    }

    // 3. Add a method to load airports from database
    private void loadAirports() {
        try (Connection conn = DbConnection.getInstance()) {
            // Get the airports table model
            JTable airportsTable = null;
            JTabbedPane tabbedPane = null;

            // Find the airports table in the tabbed pane
            for (Component comp : mainPanel.getComponents()) {
                if (comp.isVisible() && comp instanceof JPanel) {
                    for (Component child : ((JPanel) comp).getComponents()) {
                        if (child instanceof JTabbedPane) {
                            tabbedPane = (JTabbedPane) child;
                            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                                if (tabbedPane.getTitleAt(i).equals("Airports")) {
                                    Component tabComp = tabbedPane.getComponentAt(i);
                                    if (tabComp instanceof JPanel) {
                                        for (Component tabChild : ((JPanel) tabComp).getComponents()) {
                                            if (tabChild instanceof JScrollPane) {
                                                JScrollPane scrollPane = (JScrollPane) tabChild;
                                                if (scrollPane.getViewport().getView() instanceof JTable) {
                                                    airportsTable = (JTable) scrollPane.getViewport().getView();
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                                if (airportsTable != null)
                                    break;
                            }
                        }
                        if (airportsTable != null)
                            break;
                    }
                }
                if (airportsTable != null)
                    break;
            }

            if (airportsTable == null)
                return;

            DefaultTableModel model = (DefaultTableModel) airportsTable.getModel();
            model.setRowCount(0); // Clear existing data

            // Fetch airports from database
            ArrayList<Airport> airports = Airport.getAll();

            for (Airport airport : airports) {
                // Fetch country name (placeholder - you'll need to implement this)
                String countryName = getCountryName(airport.getCountryId());

                model.addRow(new Object[] {
                        airport.getId(),
                        airport.getCode(),
                        airport.getName(),
                        countryName,
                        airport.getAddress(),
                        airport.getStatus(),
                        "Edit/Delete"
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading airports: " + e.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to get country name from country ID
    private String getCountryName(int countryId) {
        // This would normally query the database to get the country name
        // For now, return a placeholder
        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement("SELECT name FROM country WHERE id = ?")) {
            stmt.setInt(1, countryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    // 4. Add dialog for creating/editing airports
    private void showAddAirportDialog() {
        showEditAirportDialog(null);
    }

    private void showEditAirportDialog(Airport airport) {
        JDialog dialog = new JDialog(this, airport == null ? "Add New Airport" : "Edit Airport", true);
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Form fields
        JTextField codeField = new JTextField(5);
        if (airport != null)
            codeField.setText(airport.getCode());
        codeField.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, javax.swing.text.AttributeSet a)
                    throws javax.swing.text.BadLocationException {
                if (str == null)
                    return;
                if ((getLength() + str.length()) <= 3) {
                    super.insertString(offs, str.toUpperCase(), a);
                }
            }
        });

        JTextField nameField = new JTextField(20);
        if (airport != null)
            nameField.setText(airport.getName());

        JComboBox<String> countryCombo = new JComboBox<>();
        fillCountryComboBox(countryCombo);

        JTextField addressField = new JTextField(20);
        if (airport != null)
            addressField.setText(airport.getAddress());

        JComboBox<String> statusCombo = new JComboBox<>(new String[] { "Active", "Inactive", "Under Maintenance" });
        if (airport != null)
            statusCombo.setSelectedItem(airport.getStatus());

        panel.add(createDialogField("Airport Code (3 letters):", codeField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Airport Name:", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Country:", countryCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Address:", addressField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Status:", statusCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                // Validate inputs
                String code = codeField.getText().trim();
                String name = nameField.getText().trim();
                String address = addressField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();
                String countryName = (String) countryCombo.getSelectedItem();

                if (code.isEmpty() || name.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all required fields.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (code.length() != 3) {
                    JOptionPane.showMessageDialog(dialog,
                            "Airport code must be exactly 3 letters.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Get country ID
                int countryId = getCountryIdByName(countryName);

                // Save or update airport
                if (airport == null) {
                    // Create new airport
                    Airport newAirport = new Airport(code, name, address, status);
                    newAirport.setCountryId(countryId);
                    newAirport.save();
                    JOptionPane.showMessageDialog(dialog, "Airport added successfully!");
                } else {
                    // Update existing airport
                    airport.setCode(code);
                    airport.setName(name);
                    airport.setAddress(address);
                    airport.setStatus(status);
                    airport.setCountryId(countryId);
                    updateAirport(airport);
                    JOptionPane.showMessageDialog(dialog, "Airport updated successfully!");
                }

                dialog.dispose();
                loadAirports(); // Refresh the list

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error saving airport: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Helper method to update an existing airport
    private void updateAirport(Airport airport) throws SQLException {
        String sql = "UPDATE airport SET code = ?, name = ?, address = ?, status = ?, country_id = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, airport.getCode());
            stmt.setString(2, airport.getName());
            stmt.setString(3, airport.getAddress());
            stmt.setString(4, airport.getStatus());
            stmt.setInt(5, airport.getCountryId());
            stmt.setInt(6, airport.getId());
            stmt.executeUpdate();
        }
    }

    // Get country ID by name
    private int getCountryIdByName(String countryName) throws SQLException {
        int countryId = 1; // Default to ID 1 if not found

        String sql = "SELECT id FROM country WHERE name = ?";
        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, countryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    countryId = rs.getInt("id");
                } else {
                    // Create country if it doesn't exist
                    sql = "INSERT INTO country (name) VALUES (?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(sql,
                            PreparedStatement.RETURN_GENERATED_KEYS)) {
                        insertStmt.setString(1, countryName);
                        insertStmt.executeUpdate();
                        try (ResultSet keys = insertStmt.getGeneratedKeys()) {
                            if (keys.next()) {
                                countryId = keys.getInt(1);
                            }
                        }
                    }
                }
            }
        }

        return countryId;
    }

    // Fill country combo box with existing countries
    private void fillCountryComboBox(JComboBox<String> comboBox) {
        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement("SELECT name FROM country ORDER BY name");
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                comboBox.addItem(rs.getString("name"));
            }

            // Add some common countries if the list is empty
            if (comboBox.getItemCount() == 0) {
                String[] commonCountries = { "United States", "Canada", "United Kingdom", "France", "Germany",
                        "Japan", "China", "Australia", "Brazil", "India" };
                for (String country : commonCountries) {
                    comboBox.addItem(country);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Add default items on error
            comboBox.addItem("United States");
            comboBox.addItem("Canada");
            comboBox.addItem("United Kingdom");
        }
    }

    // 5. Methods for searching and filtering airports
    private void searchAirports(String query) {
        try (Connection conn = DbConnection.getInstance()) {
            String sql = "SELECT a.*, c.name as country_name FROM airport a " +
                    "LEFT JOIN country c ON a.country_id = c.id " +
                    "WHERE a.code LIKE ? OR a.name LIKE ? OR c.name LIKE ? OR a.address LIKE ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchPattern = "%" + query + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);

                // Get the airports table model
                JTable airportsTable = findAirportsTable();
                if (airportsTable == null)
                    return;

                DefaultTableModel model = (DefaultTableModel) airportsTable.getModel();
                model.setRowCount(0); // Clear existing data

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        model.addRow(new Object[] {
                                rs.getInt("id"),
                                rs.getString("code"),
                                rs.getString("name"),
                                rs.getString("country_name"),
                                rs.getString("address"),
                                rs.getString("status"),
                                "Edit/Delete"
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching airports: " + e.getMessage(),
                    "Search Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterAirportsByStatus(String status) {
        try (Connection conn = DbConnection.getInstance()) {
            String sql;
            PreparedStatement stmt;

            if ("All Statuses".equals(status)) {
                sql = "SELECT a.*, c.name as country_name FROM airport a " +
                        "LEFT JOIN country c ON a.country_id = c.id";
                stmt = conn.prepareStatement(sql);
            } else {
                sql = "SELECT a.*, c.name as country_name FROM airport a " +
                        "LEFT JOIN country c ON a.country_id = c.id " +
                        "WHERE a.status = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, status);
            }

            // Get the airports table model
            JTable airportsTable = findAirportsTable();
            if (airportsTable == null)
                return;

            DefaultTableModel model = (DefaultTableModel) airportsTable.getModel();
            model.setRowCount(0); // Clear existing data

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[] {
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("name"),
                            rs.getString("country_name"),
                            rs.getString("address"),
                            rs.getString("status"),
                            "Edit/Delete"
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error filtering airports: " + e.getMessage(),
                    "Filter Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper method to find the airports table
    private JTable findAirportsTable() {
        JTabbedPane tabbedPane = null;

        // Find the tabbed pane in the system config panel
        for (Component comp : mainPanel.getComponents()) {
            if (comp instanceof JPanel && comp.isVisible()) {
                JPanel visiblePanel = (JPanel) comp;
                for (Component child : visiblePanel.getComponents()) {
                    if (child instanceof JTabbedPane) {
                        tabbedPane = (JTabbedPane) child;
                        break;
                    }
                }
            }
            if (tabbedPane != null)
                break;
        }

        if (tabbedPane == null)
            return null;

        // Find the airports tab and table
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if ("Airports".equals(tabbedPane.getTitleAt(i))) {
                Component tabComp = tabbedPane.getComponentAt(i);
                if (tabComp instanceof JPanel) {
                    JPanel airportsPanel = (JPanel) tabComp;
                    for (Component child : airportsPanel.getComponents()) {
                        if (child instanceof JScrollPane) {
                            JScrollPane scrollPane = (JScrollPane) child;
                            if (scrollPane.getViewport().getView() instanceof JTable) {
                                return (JTable) scrollPane.getViewport().getView();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    // 6. Add a button renderer and editor for the actions column
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private AdminDashboard parent;
        private String type;

        public ButtonEditor(JCheckBox checkBox, AdminDashboard parent, String type) {
            super(checkBox);
            this.parent = parent;
            this.type = type;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            this.table = table;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.getSelectedRow();
                int id = (Integer) table.getValueAt(row, 0);

                // Show a popup menu with Edit and Delete options
                JPopupMenu menu = new JPopupMenu();
                JMenuItem editItem = new JMenuItem("Edit");
                JMenuItem deleteItem = new JMenuItem("Delete");

                if ("airport".equals(type)) {
                    editItem.addActionListener(e -> editAirport(id));
                    deleteItem.addActionListener(e -> deleteAirport(id));
                }

                menu.add(editItem);
                menu.add(deleteItem);
                menu.show(button, 0, button.getHeight());
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // 7. Methods for editing and deleting airports
    private void editAirport(int airportId) {
        try {
            Airport airport = Airport.load(airportId);
            showEditAirportDialog(airport);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading airport: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAirport(int airportId) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this airport?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM airport WHERE id = ?")) {
            stmt.setInt(1, airportId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Airport deleted successfully!");
                loadAirports(); // Refresh the list
            } else {
                JOptionPane.showMessageDialog(this,
                        "No airport found with ID " + airportId,
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();

            // Check if this is a foreign key constraint violation
            if (e.getMessage().contains("foreign key constraint")) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete this airport because it is referenced by flights or other records.\n" +
                                "Consider marking it as inactive instead.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error deleting airport: " + e.getMessage(),
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
    Container container = getContentPane(); // or mainFrame.getContentPane()

    // Remove the old panel
    container.remove(dashboardPanel);

    // Create a new one with fresh data
    dashboardPanel = createDashboardPanel();

    // Add the new one back
    container.add(dashboardPanel, BorderLayout.CENTER);

    // Refresh the UI
    container.revalidate();
    container.repaint();
}

    private void loadUsers() {
        String sql = """
                    SELECT u.id, u.username, u.email, u.phone, u.age, r.role_name, u.created_at
                    FROM user u
                    JOIN role r ON u.role_id = r.id
                """;

        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) usersTable.getModel();
            model.setRowCount(0); // Clear existing data

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                int age = rs.getInt("age");
                String roleName = rs.getString("role_name");
                String createdAt = rs.getString("created_at");

                model.addRow(new Object[] { id, username, email, phone, age, roleName, createdAt, "Edit/Delete" });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading users: " + e.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFlights() {
        String query = """
                    SELECT
                        f.id,
                        dep.name AS departure_airport,
                        arr.name AS arrival_airport,
                        f.gate,
                        a.model AS aircraft_model,
                        CONCAT(fs.dayOfWeek, ' ', fs.departure_time) AS schedule,
                        f.duration
                    FROM flight f
                    JOIN airport dep ON f.departure_airport_id = dep.id
                    JOIN airport arr ON f.arrival_airport_id = arr.id
                    JOIN aircraft a ON f.aircraft_id = a.id
                    JOIN weeklyschedule fs ON f.flight_schedule_id = fs.id
                """;

        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            DefaultTableModel model = (DefaultTableModel) flightsTable.getModel();
            model.setRowCount(0); // Clear existing data

            while (rs.next()) {
                int id = rs.getInt("id");
                String departure = rs.getString("departure_airport");
                String arrival = rs.getString("arrival_airport");
                String gate = rs.getString("gate");
                String aircraft = rs.getString("aircraft_model");
                String schedule = rs.getString("schedule");
                String duration = rs.getString("duration");

                model.addRow(new Object[] { id, departure, arrival, gate, aircraft, schedule, duration });
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading flights: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // Dialog methods
    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "Add New User", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Fields
        JTextField usernameField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);
        JTextField ageField = new JTextField(20);
        JComboBox<String> roleCombo = new JComboBox<>(new String[] {
                "User", "Admin"
        });

        panel.add(createDialogField("Username:", usernameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Email:", emailField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Phone:", phoneField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Password:", passwordField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Confirm Password:", confirmPasswordField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Age:", ageField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Role:", roleCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(_ -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            String ageText = ageField.getText().trim();
            String selectedRoleName = (String) roleCombo.getSelectedItem();

            // Validate inputs
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                    || ageText.isEmpty()) {
                showError("Please fill in all required fields.");
                return;
            }

            if (!password.equals(confirmPassword)) {
                showError("Passwords do not match.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                showError("Age must be a number.");
                return;
            }

            try {
                // Check for existing user by email
                User existingUser = User.loadWithEmail(email);
                if (existingUser != null) {
                    showError("Email already registered.");
                    return;
                }

                // Load or create role
                Role selectedRole = Role.loadByName(selectedRoleName);
                if (selectedRole == null) {
                    selectedRole = new Role(selectedRoleName);
                    selectedRole.save(); // Sets role ID
                }

                if (selectedRole == null || selectedRole.getId() <= 0) {
                    showError("Error: Role could not be created or retrieved.");
                    return;
                }

                // Register new user
                User newUser = new User(username, email, phone, password, age, selectedRole);
                newUser.register();

                JOptionPane.showMessageDialog(dialog, "User added successfully!");
                dialog.dispose();
                loadUsers(); // Refresh list
            } catch (SQLException ex) {
                ex.printStackTrace();
                showError("Database error: " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                showError("Unexpected error: " + ex.getMessage());
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddFlightDialog() {
        JDialog dialog = new JDialog(this, "Add New Flight", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Load airports from database
        JComboBox<String> departureCombo = new JComboBox<>();
        JComboBox<String> arrivalCombo = new JComboBox<>();
        JComboBox<String> airCraftCombo = new JComboBox<>();

        loadAirportsIntoCombobox(departureCombo, arrivalCombo);
        loadAircraftsIntoCombobox(airCraftCombo);

        panel.add(createDialogField("Departure Airport:", departureCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Arrival Airport:", arrivalCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("Aircraft:", airCraftCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        // 2. Aircraft selection - let admin type name
        JTextField aircraftField = new JTextField();
        JButton searchAircraftBtn = new JButton("Find");

        JPanel aircraftPanel = new JPanel(new BorderLayout(5, 5));
        aircraftPanel.add(aircraftField, BorderLayout.CENTER);
        aircraftPanel.add(searchAircraftBtn, BorderLayout.EAST);

        // 3. Schedule information
        JComboBox<String> dayCombo = new JComboBox<>(new String[] {
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        });
        panel.add(createDialogField("Day:", dayCombo));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextField departureTimeField = new JTextField("08:00");
        panel.add(createDialogField("Departure Time (HH:MM):", departureTimeField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextField durationField = new JTextField("360");
        panel.add(createDialogField("Duration (minutes):", durationField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextField gateField = new JTextField("A1");
        panel.add(createDialogField("Gate:", gateField));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(_ -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            try {
                // Validate airports are different
                String departureAirportCode = ((String) departureCombo.getSelectedItem()).split(" - ")[0];
                String arrivalAirportCode = ((String) arrivalCombo.getSelectedItem()).split(" - ")[0];

                if (departureAirportCode.equals(arrivalAirportCode)) {
                    JOptionPane.showMessageDialog(dialog,
                            "Departure and arrival airports must be different",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String aircraftSelected = ((String) airCraftCombo.getSelectedItem());

                // Get other values
                String day = (String) dayCombo.getSelectedItem();
                String departureTimeStr = departureTimeField.getText();
                int duration = Integer.parseInt(durationField.getText());
                String gate = gateField.getText();

                // Parse time and create schedule
                Time departureTime = Time.valueOf(departureTimeStr + ":00");
                model.DayOfWeek dayOfWeek = model.DayOfWeek.valueOf(day.toUpperCase());

                WeeklySchedule schedule = new WeeklySchedule(dayOfWeek, departureTime);
                schedule.save();

                // Get airport IDs
                int departureAirportId = getAirportIdByCode(departureAirportCode);
                int arrivalAirportId = getAirportIdByCode(arrivalAirportCode);
                int aircraftId = getAircraftIdByCode(aircraftSelected);

                // Create the flight
                String sql = "INSERT INTO flight (departure_airport_id, arrival_airport_id, gate, " +
                        "duration, flight_schedule_id, aircraft_id) VALUES (?, ?, ?, ?, ?, ?)";

                try (Connection conn = DbConnection.getInstance();
                        PreparedStatement stmt = conn.prepareStatement(sql)) {

                    stmt.setInt(1, departureAirportId);
                    stmt.setInt(2, arrivalAirportId);
                    stmt.setString(3, gate);
                    stmt.setInt(4, duration);
                    stmt.setInt(5, schedule.getId());
                    stmt.setInt(6, aircraftId);

                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows > 0) {
                        JOptionPane.showMessageDialog(dialog, "Flight added successfully!");
                        dialog.dispose();
                        loadFlights();
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Helper method to load airports
    private void loadAirportsIntoCombobox(JComboBox<String> departureCombo, JComboBox<String> arrivalCombo) {
        try {
            String sql = "SELECT code, name FROM airport ORDER BY code";
            try (Connection conn = DbConnection.getInstance();
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {

                departureCombo.removeAllItems();
                arrivalCombo.removeAllItems();

                while (rs.next()) {
                    String display = rs.getString("code") + " - " + rs.getString("name");
                    departureCombo.addItem(display);
                    arrivalCombo.addItem(display);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadAircraftsIntoCombobox(JComboBox<String> aircraftCombo) {
        try {
            String sql = "SELECT model FROM aircraft";
            try (Connection conn = DbConnection.getInstance();
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String display = rs.getString("model");
                    aircraftCombo.addItem(display);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper methods you'll need to add:
    private int getAircraftIdByCode(String code) throws SQLException {
        String sql = "SELECT id FROM aircraft WHERE model = ?";
        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new SQLException("Aircraft not found: " + code);
            }
        }
    }

    private int getAirportIdByCode(String code) throws SQLException {
        String sql = "SELECT id FROM airport WHERE code = ?";
        try (Connection conn = DbConnection.getInstance();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new SQLException("Airport not found: " + code);
            }
        }
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

    // AIrline management
    private JPanel createAirlineManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Header
        JLabel headerLabel = new JLabel("Airline Management");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(headerLabel, BorderLayout.NORTH);

        // Airline table
        String[] columns = { "ID", "Name", "Code", "Aircraft Count"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only actions column is editable
            }
        };

        JTable airlineTable = new JTable(model);
        airlineTable.setFillsViewportHeight(true);

        // Add mouse listener for action buttons
        airlineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = airlineTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / airlineTable.getRowHeight();

                if (row < airlineTable.getRowCount() && row >= 0 &&
                        column < airlineTable.getColumnCount() && column >= 0) {
                    Object value = airlineTable.getValueAt(row, column);
                    if (value instanceof String && "Edit".equals(value)) {
                        int airlineId = (int) airlineTable.getValueAt(row, 0);
                        showEditAirlineDialog(airlineId);
                    } else if (value instanceof String && "Delete".equals(value)) {
                        int airlineId = (int) airlineTable.getValueAt(row, 0);
                        deleteAirline(airlineId, row);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(airlineTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Add Airline");
        addButton.addActionListener(_ -> showAddAirlineDialog());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(_ -> loadAirlines(airlineTable));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(_ -> searchAirlines(searchField.getText(), airlineTable));

        controlPanel.add(addButton);
        controlPanel.add(refreshButton);
        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);

        panel.add(controlPanel, BorderLayout.SOUTH);

        // Load airlines when panel is created
        loadAirlines(airlineTable);

        return panel;
    }

    private void loadAirlines(JTable airlineTable) {
        try {
            DefaultTableModel model = (DefaultTableModel) airlineTable.getModel();
            model.setRowCount(0); // Clear existing data

            String sql = "SELECT a.id, a.name, a.code, COUNT(ac.id) as aircraft_count " +
                    "FROM airline a LEFT JOIN aircraft ac ON a.id = ac.airline_id " +
                    "GROUP BY a.id, a.name, a.code";

            try (Connection conn = DbConnection.getInstance();
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String code = rs.getString("code");
                    int aircraftCount = rs.getInt("aircraft_count");

                    model.addRow(new Object[] {
                            id, name, code, aircraftCount,
                           
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading airlines: " + e.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddAirlineDialog() {
        JDialog dialog = new JDialog(this, "Add New Airline", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Fields
        JTextField nameField = new JTextField(20);
        JTextField codeField = new JTextField(3); // Airline codes are typically 2-3 characters

        panel.add(createDialogField("Airline Name:", nameField));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createDialogField("IATA Code:", codeField));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim().toUpperCase();

            if (name.isEmpty() || code.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Please fill in all fields",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (code.length() < 2 || code.length() > 3) {
                JOptionPane.showMessageDialog(dialog,
                        "Airline code must be 2-3 characters",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                Airline airline = new Airline(name, code);
                airline.save();

                JOptionPane.showMessageDialog(dialog,
                        "Airline added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                dialog.dispose();
                // Refresh the airline table in the parent panel
                loadAirlines((JTable) ((JScrollPane) ((JPanel) dialog.getParent().getParent().getParent())
                        .getComponent(1)).getViewport().getView());
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog,
                        "Error saving airline: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditAirlineDialog(int airlineId) {
        try {
            Airline airline = Airline.load(airlineId);

            JDialog dialog = new JDialog(this, "Edit Airline", true);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(new EmptyBorder(20, 20, 20, 20));

            // Fields
            JTextField nameField = new JTextField(airline.getName(), 20);
            JTextField codeField = new JTextField(airline.getCode(), 3);

            panel.add(createDialogField("Airline Name:", nameField));
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(createDialogField("IATA Code:", codeField));
            panel.add(Box.createRigidArea(new Dimension(0, 20)));

            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dialog.dispose());

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(e -> {
                String name = nameField.getText().trim();
                String code = codeField.getText().trim().toUpperCase();

                if (name.isEmpty() || code.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please fill in all fields",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (code.length() < 2 || code.length() > 3) {
                    JOptionPane.showMessageDialog(dialog,
                            "Airline code must be 2-3 characters",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    airline.setName(name);
                    airline.setCode(code);

                    // Update in database
                    String sql = "UPDATE airline SET name = ?, code = ? WHERE id = ?";
                    try (Connection conn = DbConnection.getInstance();
                            PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, name);
                        stmt.setString(2, code);
                        stmt.setInt(3, airlineId);
                        stmt.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(dialog,
                            "Airline updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                    // Refresh the airline table in the parent panel
                    loadAirlines((JTable) ((JScrollPane) ((JPanel) dialog.getParent().getParent().getParent())
                            .getComponent(1)).getViewport().getView());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog,
                            "Error updating airline: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            });

            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            panel.add(buttonPanel);

            dialog.add(panel);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading airline: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAirline(int airlineId, int row) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this airline? This will also delete all associated aircraft.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // First delete all aircraft associated with this airline
                String deleteAircraftSql = "DELETE FROM aircraft WHERE airline_id = ?";
                try (Connection conn = DbConnection.getInstance();
                        PreparedStatement stmt = conn.prepareStatement(deleteAircraftSql)) {
                    stmt.setInt(1, airlineId);
                    stmt.executeUpdate();
                }

                // Then delete the airline
                String deleteAirlineSql = "DELETE FROM airline WHERE id = ?";
                try (Connection conn = DbConnection.getInstance();
                        PreparedStatement stmt = conn.prepareStatement(deleteAirlineSql)) {
                    stmt.setInt(1, airlineId);
                    stmt.executeUpdate();
                }

                // Remove from table
                DefaultTableModel model = (DefaultTableModel) ((JTable) ((JScrollPane) ((JPanel) getContentPane()
                        .getComponent(0)).getComponent(1))
                        .getViewport().getView()).getModel();

                model.removeRow(row);

                JOptionPane.showMessageDialog(this,
                        "Airline deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error deleting airline: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchAirlines(String query, JTable airlineTable) {
        try {
            DefaultTableModel model = (DefaultTableModel) airlineTable.getModel();
            model.setRowCount(0); // Clear existing data

            String sql = "SELECT a.id, a.name, a.code, COUNT(ac.id) as aircraft_count " +
                    "FROM airline a LEFT JOIN aircraft ac ON a.id = ac.airline_id " +
                    "WHERE a.name LIKE ? OR a.code LIKE ? " +
                    "GROUP BY a.id, a.name, a.code";

            try (Connection conn = DbConnection.getInstance();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + query + "%");
                stmt.setString(2, "%" + query + "%");

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String code = rs.getString("code");
                        int aircraftCount = rs.getInt("aircraft_count");

                        model.addRow(new Object[] {
                                id, name, code, aircraftCount,
                                "Edit/Delete"
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error searching airlines: " + e.getMessage(),
                    "Data Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
