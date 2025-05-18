package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ArrayList;
import util.*;
public class Country {
    private int id;
    private String name;
    private ArrayList<Airport> airports;

    public Country(String name) {
        this.name = name;
        this.airports = new ArrayList<>();
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO country (name) VALUES (?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static Country load(int id) throws SQLException {
        String sql = "SELECT * FROM country WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Country country = new Country(rs.getString("name"));
                    country.setId(id);
                    return country;
                }
                throw new SQLException("Country not found");
            }
        }
    }
    public static ArrayList<Country> loadAll() throws SQLException {
        String sql = "SELECT * FROM country";
        ArrayList<Country> countries = new ArrayList<>();
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int id = rs.getInt("id");
                    Country country = new Country(name);
                    country.setId(id);
                    countries.add(country);
                }
                return countries;
            }
        }
    }

    public ArrayList<Airport> getAirports() throws SQLException {
        if (airports.isEmpty()) {
            ArrayList<Integer> airportIds = new ArrayList<>();
    
            String sql = "SELECT id FROM airport WHERE country_id = ?";
            try (Connection conn = DbConnection.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
    
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        airportIds.add(rs.getInt("id")); // Just collect IDs for now
                    }
                }
            }
    
            // After ResultSet and connection are closed, safely load the airports
            for (int airportId : airportIds) {
                Airport airport = Airport.load(airportId);
                if (airport != null) {
                    airports.add(airport);
                }
            }
        }
    
        return airports;
    }
    

    public void addAirport(Airport airport) throws SQLException {
        airport.setCountryId(id);
        airport.save();
        airports.add(airport);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    @Override
public String toString() {
    return this.name;
}
}