package model;

import java.sql.*;
import java.util.ArrayList;
import util.DbConnection;


public class Role {
    private int id;
    private String roleName;
    private String description;
    
    public Role(int id, String roleName, String description) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
    }
    
    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public Role(String roleName) {
        this.roleName = roleName;
    }
    
    public void save() throws SQLException {
        String sql = "INSERT INTO role (role_name, description) VALUES (?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, roleName);
            // stmt.setString(2, !description.isEmpty()?description:null);
            stmt.setString(2, (description != null && !description.isEmpty()) ? description : null);

            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }
    
    
    public static Role loadByName(String roleName) throws SQLException {
        String sql = "SELECT id, role_name FROM role WHERE role_name = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, roleName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("role_name");
                    return new Role(name);
                } else {
                    return null; // Role not found
                }
            }
        }
    }
    public static Role load(int id) throws SQLException {
        String sql = "SELECT * FROM role WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String roleName = rs.getString("role_name");
                    String description = rs.getString("description");
                    return new Role(id, roleName, description);
                } else {
                    throw new SQLException("Role not found");
                }
            }
        }
    }
    
    public static ArrayList<Role> loadAll() throws SQLException {
        ArrayList<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM role";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String roleName = rs.getString("role_name");
                String description = rs.getString("description");
                roles.add(new Role(id, roleName, description));
            }
        }
        return roles;
    }
    
    public void update() throws SQLException {
        String sql = "UPDATE role SET role_name = ?, description = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, roleName);
            stmt.setString(2, description);
            stmt.setInt(3, id);
            
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Update failed");
            }
        }
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        return roleName;
    }
}
