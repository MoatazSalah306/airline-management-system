package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.JTextArea;

import model.Seat;
import util.*;

public class FlightReservation {
    private int id;
    private int flightId;
    private int userId;
    private String qrCode;
    private Date bookingDate;
    private ReservationStatus status;
    private Map<Passenger, Seat> passengerSeatMap;

    public FlightReservation(int flightId, Date bookingDate) {
        this.flightId = flightId;
        this.bookingDate = bookingDate;
        this.status = ReservationStatus.REQUESTED;
        this.passengerSeatMap = new HashMap<>();
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO flightReservation (flight_id, user_id, qr_code, booking_date, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, flightId);
            stmt.setInt(2, userId);
            stmt.setString(3, qrCode);
            stmt.setDate(4, bookingDate);
            stmt.setString(5, status.name());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                    
                }
            }
        }
    }

    public static FlightReservation load(int id) throws SQLException {
        String sql = "SELECT * FROM flightReservation WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Extract all needed values from ResultSet FIRST
                    int flightId = rs.getInt("flight_id");
                    Date bookingDate = rs.getDate("booking_date");
                    int userId = rs.getInt("user_id");
                    String qrCode = rs.getString("qr_code");
                    String statusStr = rs.getString("status");
    
                    // Now call any other DB methods safely
                    FlightReservation reservation = new FlightReservation(flightId, bookingDate);
                    reservation.setId(id);
                    reservation.setUserId(userId);
                    reservation.setQrCode(qrCode);
                    reservation.setStatus(ReservationStatus.valueOf(statusStr.toUpperCase()));
                    return reservation;
                }
                throw new SQLException("FlightReservation not found");
            }
        }
    }
    

    public Map<Passenger, Seat> getPassengerSeatMap() throws SQLException {
        if (passengerSeatMap.isEmpty()) {
            String sql = "SELECT passenger_id, seat_id FROM passenger_seat WHERE flightReservation_id = ?";
            try (Connection conn = DbConnection.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int seatId= rs.getInt("seat_id");
                        int passengerId= rs.getInt("passenger_id");
                        Passenger passenger = Passenger.load(passengerId);
                        Seat seat = Seat.load(seatId);
                        passengerSeatMap.put(passenger, seat);
                    }
                }
            }
        }
        return passengerSeatMap;
    }

    public void addPassengerSeat(Passenger passenger, Seat seat) throws SQLException {
        passenger.save();
        String sql = "INSERT INTO passenger_seat (passenger_id, seat_id, flightReservation_id) VALUES (?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, passenger.getId());
            stmt.setInt(2, seat.getId());
            stmt.setInt(3, id);
            stmt.executeUpdate();
            passengerSeatMap.put(passenger, seat);
        }
    }

    public boolean makeReservation() throws SQLException {
        this.status = ReservationStatus.CONFIRMED;
        String sql = "UPDATE flightReservation SET status = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }
    public boolean updateState( ReservationStatus reservationStatus) throws SQLException {
        String sql = "UPDATE flightReservation SET status = ? WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, reservationStatus.name());
            stmt.setInt(2, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public ArrayList<String> getHistoryFromDB() throws SQLException {
        ArrayList<String> history = new ArrayList<>();
        String sql = "SELECT * FROM flightReservation WHERE user_id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String record = String.format("Reservation ID: %d | Flight ID: %d | User ID: %d | QR Code: %s | Booking Date: %s | Status: %s",
                        rs.getInt("id"),
                        rs.getInt("flight_id"),
                        rs.getInt("user_id"),
                        rs.getString("qr_code") != null ? rs.getString("qr_code") : "N/A",
                        rs.getDate("booking_date") != null ? rs.getDate("booking_date").toString() : "N/A",
                        rs.getString("status") != null ? rs.getString("status") : "N/A");
                    history.add(record);
                }
            }
        }
        return history;
    }

    public void refreshHistory(JTextArea historyArea) throws SQLException {
        historyArea.setText("");
        ArrayList<String> history = getHistoryFromDB();
        for (String record : history) {
            historyArea.append(record + "\n\n");
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getFlightId() { return flightId; }
    public void setFlightId(int flightId) { this.flightId = flightId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}