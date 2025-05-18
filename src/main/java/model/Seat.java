package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import util.*;
public class Seat {
    private int id;
    private int aircraftId;
    private SeatClass seatClass;
    private String  seatNumber;

    public Seat(SeatClass seatClass,String  seatNumber) {
        this.seatClass = seatClass;
        this.seatNumber = seatNumber;
    }

    public void save(int aircraftId) throws SQLException {
        this.aircraftId = aircraftId;
        String sql = "INSERT INTO seat (aircraft_id, class,seat_number) VALUES (?, ?,?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, aircraftId);
            stmt.setString(2, seatClass.name());
            stmt.setString(3, seatNumber);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static Seat load(int id) throws SQLException {
        String sql = "SELECT * FROM seat WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Seat seat = new Seat(SeatClass.valueOf(rs.getString("class")),rs.getString("seat_number"));
                    seat.setId(id);
                    seat.setAircraftId(rs.getInt("aircraft_id"));
                    return seat;
                }
                throw new SQLException("Seat not found");
            }
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAircraftId() { return aircraftId; }
    public void setAircraftId(int aircraftId) { this.aircraftId = aircraftId; }
    public SeatClass getSeatClass() { return seatClass; }
    public void setSeatClass(SeatClass seatClass) { this.seatClass = seatClass; }
    public String getSeatNumber() {
        return seatNumber;
    }
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}