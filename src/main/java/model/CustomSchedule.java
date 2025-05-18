package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import util.*;
public class CustomSchedule {
    private int id;
    private int flightId;
    private Time departureTime;
    private Date customDate;

    public CustomSchedule(Time departureTime, Date customDate) {
        this.departureTime = departureTime;
        this.customDate = customDate;
    }

    public void save(int flightId) throws SQLException {
        this.flightId = flightId;
        String sql = "INSERT INTO customSchedule (flight_id, departure_time, customDate) VALUES (?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, flightId);
            stmt.setTime(2, departureTime);
            stmt.setDate(3, customDate);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static CustomSchedule load(int id) throws SQLException {
        String sql = "SELECT * FROM customSchedule WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CustomSchedule schedule = new CustomSchedule(
                        rs.getTime("departure_time"),
                        rs.getDate("customDate")
                    );
                    schedule.setId(id);
                    schedule.setFlightId(rs.getInt("flight_id"));
                    return schedule;
                }
                throw new SQLException("CustomSchedule not found");
            }
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    public Time getDepartureTime() { return departureTime; }
    public void setDepartureTime(Time departureTime) { this.departureTime = departureTime; }
    public Date getCustomDate() { return customDate; }
    public void setCustomDate(Date customDate) { this.customDate = customDate; }
}