package model;

import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import util.*;

public class User {
    private int id;
    private String username;
    private String email;
    private String phone;
    private String password; // Now stores hash:salt format
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer age;
    private int roleId;
    private Role role; // Added role object
    private ArrayList<FlightReservation> reservations = new ArrayList<>();

    public User(String username, String email, String phone, String password, Integer age, Role role) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        // Hash the password before storing
        this.password = SecurityUtil.generateSecurePassword(password);
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.age = age;
        this.roleId = role.getId();
        this.role = role;
    }
    
    // Constructor for loading from database (password already hashed)
    private User(int id, String username, String email, String phone, String hashedPassword, 
                Timestamp createdAt, Timestamp updatedAt, Integer age, int roleId) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.password = hashedPassword;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.age = age;
        this.roleId = roleId;
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO user (username, email, phone, password, created_at, age, role_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, password);
            stmt.setTimestamp(5, createdAt);
            stmt.setObject(6, age);
            stmt.setInt(7, roleId);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static User loadWithId(int id) throws SQLException {
        String sql = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String username = rs.getString("username");
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String password = rs.getString("password");
                    Integer age = rs.getInt("age") == 0 ? null : rs.getInt("age");
                    int roleId = rs.getInt("role_id");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    Timestamp updatedAt = rs.getTimestamp("updated_at");

                    User user = new User(userId, username, email, phone, password, createdAt, updatedAt, age, roleId);
                    
                    // Load role
                    user.role = Role.load(roleId);
                    
                    return user;
                } else {
                    throw new SQLException("User not found");
                }
            }
        }
    }

    public static User loadWithEmail(String email) throws SQLException {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String emailValue = rs.getString("email");
                    String phone = rs.getString("phone");
                    String password = rs.getString("password");
                    int ageValue = rs.getInt("age");
                    Integer age = (rs.wasNull() || ageValue == 0) ? null : ageValue;
                    int roleId = rs.getInt("role_id");
                    Timestamp createdAt = rs.getTimestamp("created_at");
                    Timestamp updatedAt = rs.getTimestamp("updated_at");

                    User user = new User(id, username, emailValue, phone, password, createdAt, updatedAt, age, roleId);
                    
                    // Load role
                    user.role = Role.load(roleId);
                    
                    return user;
                } else {
                    throw new SQLException("User not found");
                }
            }
        }
    }

    public void register() throws SQLException {
        save();
    }
    
    /**
     * Verify if the provided password matches the stored password
     * @param inputPassword The password to verify
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String inputPassword) {
        // Check if password is in new hash:salt format
        if (password.contains(":")) {
            return SecurityUtil.verifySecurePassword(inputPassword, password);
        } else {
            // Legacy password check (plain text) - for backward compatibility
            // This should be updated to secure format on successful login
            return password.equals(inputPassword);
        }
    }
    
    /**
     * Update password with secure hashing
     * @param newPassword The new password to set
     * @return true if update was successful
     */
    public boolean resetPassword(String newPassword) throws SQLException {
        // Generate secure password hash
        this.password = SecurityUtil.generateSecurePassword(newPassword);
        
        String sql = "UPDATE user SET password = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, password);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public ArrayList<FlightReservation> getReservations() throws SQLException {
        if (reservations.isEmpty()) {
            String sql = "SELECT id FROM flightReservation WHERE user_id = ?";
            ArrayList<Integer> reservationIds = new ArrayList<>();

            try (Connection conn = DbConnection.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        reservationIds.add(rs.getInt("id"));
                    }
                }
            }

            // Load reservations outside ResultSet loop to avoid ResultSet closed errors
            for (int resId : reservationIds) {
                reservations.add(FlightReservation.load(resId));
            }
        }
        return reservations;
    }

    public void addReservation(FlightReservation reservation) throws SQLException {
        reservation.setUserId(id);
        reservation.save();
        reservations.add(reservation);
    }

    public void update(String newUsername, String newEmail, String newPhone, Integer newAge) throws SQLException {
        String oldData = String.format("Old Data: Username: %s, Email: %s, Phone: %s, Age: %s",
            this.username, this.email, this.phone, this.age != null ? this.age : "N/A");
        JOptionPane.showMessageDialog(null, oldData, "Old User Data", JOptionPane.INFORMATION_MESSAGE);

        this.username = (newUsername != null && !newUsername.isEmpty()) ? newUsername : this.username;
        this.email = (newEmail != null && !newEmail.isEmpty()) ? newEmail : this.email;
        this.phone = (newPhone != null && !newPhone.isEmpty()) ? newPhone : this.phone;
        this.age = (newAge != null) ? newAge : this.age;
        this.updatedAt = new Timestamp(System.currentTimeMillis());

        String sql = "UPDATE user SET username = ?, email = ?, phone = ?, age = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.username);
            stmt.setString(2, this.email);
            stmt.setString(3, this.phone);
            stmt.setObject(4, this.age);
            stmt.setTimestamp(5, this.updatedAt);
            stmt.setInt(6, this.id);

            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Update failed");
            }
        }
    }
    
    /**
     * Check if user has a specific role
     * @param roleName The role name to check
     * @return true if user has the role, false otherwise
     */
    public boolean hasRole(String roleName) {
        if (role == null) {
            try {
                role = Role.load(roleId);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return role.getRoleName().equalsIgnoreCase(roleName);
    }
    
    /**
     * Check if user is an administrator
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("Admin");
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public Role getRole() { return role; }
    public void setRole(Role role) { 
        this.role = role; 
        this.roleId = role.getId();
    }
}
