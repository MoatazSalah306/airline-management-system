package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ArrayList;
import util.*;

public class Airline {
    private int id;
    private String name;
    private String code;
    private ArrayList<Aircraft> aircrafts;

    public Airline(String name, String code) {
        this.name = name;
        this.code = code;
        this.aircrafts = new ArrayList<>();
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO airline (name, code) VALUES (?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, code);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static Airline load(int id) throws SQLException {
        String sql = "SELECT * FROM airline WHERE id = ?";
        try (
            Connection conn = DbConnection.getInstance();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Airline airline = new Airline(rs.getString("name"), rs.getString("code"));
                    airline.setId(id);
                    return airline;
                }
                throw new SQLException("Airline not found");
            }
        }
    }

    public ArrayList<Aircraft> getAircrafts() throws SQLException {
        if (aircrafts.isEmpty()) {
            String sql = "SELECT id FROM aircraft WHERE airline_id = ?";
            try (Connection conn = DbConnection.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        aircrafts.add(Aircraft.load(rs.getInt("id")));
                    }
                }
            }
        }
        return aircrafts;
    }

    public void addAircraft(Aircraft aircraft) throws SQLException {
        aircraft.setAirlineId(id);
        aircraft.save(id);
        aircrafts.add(aircraft);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    @Override
    public String toString() {
        return this.name;
    }
    
}