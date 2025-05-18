package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import util.*;

public class Aircraft {
    private int id;
    private int airlineId;
    private String model;
    private Integer manufacturingYear;
    private ArrayList<Seat> seats;

    public Aircraft(String model, Integer manufacturingYear) {
        this.model = model;
        this.manufacturingYear = manufacturingYear;
        this.seats = new ArrayList<>();
    }


    public static Aircraft loadByModel(String model) throws SQLException {
    String sql = "SELECT * FROM aircraft WHERE model = ?";
    try (Connection conn = DbConnection.getInstance();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, model);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new Aircraft(rs.getString("model"), rs.getInt("manufacturingYear"));
            }
            throw new SQLException("Aircraft not found");
        }
    }
}

    public void save(int airlineId) throws SQLException {
        this.airlineId = airlineId;
        String sql = "INSERT INTO aircraft (airline_id, model, manufacturing_year) VALUES (?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, airlineId);
            stmt.setString(2, model);
            stmt.setObject(3, manufacturingYear);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) { // move the cursor across the returned rows
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static Aircraft load(int id) throws SQLException {
        String sql = "SELECT * FROM aircraft WHERE id = ?";
        try (
            Connection conn = DbConnection.getInstance();
            PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            // Set the parameter BEFORE executing the query
            stmt.setInt(1, id);
            
            // Now execute the query with the parameter set
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int aircraft_id = rs.getInt("airline_id");
                    Aircraft aircraft = new Aircraft(
                        rs.getString("model"),
                        rs.getInt("manufacturing_year")
                    );
                    aircraft.setId(id);
                    aircraft.setAirlineId(aircraft_id);
                    return aircraft;
                }
                throw new SQLException("Aircraft not found");
            }
        }
    }

    public ArrayList<Seat> getSeats() throws SQLException {
    if (seats.isEmpty()) {
        String sql = "SELECT id FROM seat WHERE aircraft_id = ?";
        List<Integer> seatIds = new ArrayList<>();

        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    seatIds.add(rs.getInt("id")); // Collect IDs only
                }
            }
        }

        // Load Seat objects outside the ResultSet loop
        for (int seatId : seatIds) {
            seats.add(Seat.load(seatId));
        }
    }
    return seats;
}


    public void addSeat(Seat seat) throws SQLException {
        seat.save(id);
        seats.add(seat);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getAirlineId() { return airlineId; }
    public void setAirlineId(int airlineId) { this.airlineId = airlineId; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getManufacturingYear() { return manufacturingYear; }
    public void setManufacturingYear(Integer manufacturingYear) { this.manufacturingYear = manufacturingYear; }
}