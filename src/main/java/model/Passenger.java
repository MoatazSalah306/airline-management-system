package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.*;
public class Passenger {
    private int id;
    private Integer userId; // Nullable
    private String name;
    private String passport;
    private int flightReservationId;

    public Passenger(String name, String passport) {
        this.name = name;
        this.passport = passport;
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO passenger (user_id, name, passport) VALUES (?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setObject(1, userId);
            stmt.setString(2, name);
            stmt.setString(3, passport);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static Passenger load(int id) throws SQLException {
        String sql = "SELECT * FROM passenger WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Passenger passenger = new Passenger(rs.getString("name"), rs.getString("passport"));
                    passenger.setId(id);
                    passenger.setUserId(rs.getInt("user_id"));
                    passenger.setFlightReservationId(rs.getInt("flightReservation_id"));
                    return passenger;
                }
                throw new SQLException("Passenger not found");
            }
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassport() { return passport; }
    public void setPassport(String passport) { this.passport = passport; }
    public int getFlightReservationId() { return flightReservationId; }
    public void setFlightReservationId(int flightReservationId) { this.flightReservationId = flightReservationId; }
}