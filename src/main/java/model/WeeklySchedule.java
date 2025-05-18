package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;

import util.*;
public class WeeklySchedule {
    private int id;
    private DayOfWeek dayOfWeek;
    private Time departureTime;
    private Date customDate; // Nullable in schema

    public WeeklySchedule(DayOfWeek dayOfWeek, Time departureTime, Date customDate) {
        this.dayOfWeek = dayOfWeek;
        this.departureTime = departureTime;
        this.customDate = customDate;
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO weeklySchedule (dayOfWeek, departure_time, customDate) VALUES (?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, dayOfWeek.name());
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

    public static WeeklySchedule load(int id) throws SQLException {
        String sql = "SELECT * FROM weeklySchedule WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    WeeklySchedule schedule = new WeeklySchedule(
                        DayOfWeek.valueOf(rs.getString("dayOfWeek").toUpperCase()),
                        rs.getTime("departure_time"),
                        rs.getDate("customDate")
                        
                    );
                    schedule.setId(id);
                    return schedule;
                }
                throw new SQLException("WeeklySchedule not found");
            }
        }
    }
    

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public Time getDepartureTime() { return departureTime; }
    public void setDepartureTime(Time departureTime) { this.departureTime = departureTime; }
    public Date getCustomDate() { return customDate; }
    public void setCustomDate(Date customDate) { this.customDate = customDate; }
}