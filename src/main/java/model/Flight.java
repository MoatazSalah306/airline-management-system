package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ArrayList;
import util.*;
public class Flight {
    private int id;
    private int arrivalAirportId;
    private int departureAirportId;
    private String gate;
    private Integer duration;
    private int flightScheduleId;
    private int aircraftId;
    private ArrayList<CustomSchedule> customSchedules;
    private ArrayList<Payment> payments;

    public Flight(Airport departure, Airport arrival, String gate, Integer duration, WeeklySchedule schedule, Aircraft aircraft) {
        this.departureAirportId = departure.getId();
        this.arrivalAirportId = arrival.getId();
        this.gate = gate;
        this.duration = duration;
        this.flightScheduleId = schedule.getId();
        this.aircraftId = aircraft.getId();
        this.customSchedules = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    public void save() throws SQLException {
        String sql = "INSERT INTO flight (arrival_airport_id, departure_airport_id, gate, duration, flight_schedule_id, aircraft_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, arrivalAirportId);
            stmt.setInt(2, departureAirportId);
            stmt.setString(3, gate);
            stmt.setObject(4, duration);
            stmt.setInt(5, flightScheduleId);
            stmt.setInt(6, aircraftId);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.id = rs.getInt(1);
                }
            }
        }
    }

    public static Flight load(int id) throws SQLException {
        String sql = "SELECT * FROM flight WHERE id = ?";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Read all values from the ResultSet first
                    int departureAirportId = rs.getInt("departure_airport_id");
                    int arrivalAirportId = rs.getInt("arrival_airport_id");
                    String gate = rs.getString("gate");
                    int duration = rs.getInt("duration");
                    int scheduleId = rs.getInt("flight_schedule_id");
                    int aircraftId = rs.getInt("aircraft_id");
    
                    // Now safely call other DB-dependent methods
                    Airport departure = Airport.load(departureAirportId);
                    Airport arrival = Airport.load(arrivalAirportId);
                    WeeklySchedule schedule = WeeklySchedule.load(scheduleId);
                    Aircraft aircraft = Aircraft.load(aircraftId);
    
                    Flight flight = new Flight(departure, arrival, gate, duration, schedule, aircraft);
                    flight.setId(id);
                    return flight;
                }
                throw new SQLException("Flight not found");
            }
        }
    }
    
public static List<Integer> getAllFlightIds() throws SQLException {
    List<Integer> ids = new ArrayList<>();
    String sql = "SELECT id FROM flight";
    
    try (Connection conn = DbConnection.getInstance();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            ids.add(rs.getInt("id"));
        }
    }
    
    return ids;
}


    public ArrayList<CustomSchedule> getCustomSchedules() throws SQLException {
        if (customSchedules.isEmpty()) {
            String sql = "SELECT id FROM customSchedule WHERE flight_id = ?";
            try (Connection conn = DbConnection.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        customSchedules.add(CustomSchedule.load(rs.getInt("id")));
                    }
                }
            }
        }
        return customSchedules;
    }

    public ArrayList<Payment> getPayments() throws SQLException {
        if (payments.isEmpty()) {
            String sql = "SELECT payment_id FROM flight_payment WHERE flight_id = ?";
            try (Connection conn = DbConnection.getInstance();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        payments.add(Payment.load(rs.getInt("payment_id")));
                    }
                }
            }
        }
        return payments;
    }

    public void addCustomSchedule(CustomSchedule schedule) throws SQLException {
        schedule.save(id);
        customSchedules.add(schedule);
    }

    public void addPayment(Payment payment) throws SQLException {
        payment.save();
        String sql = "INSERT INTO flight_payment (flight_id, payment_id) VALUES (?, ?)";
        try (Connection conn = DbConnection.getInstance();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.setInt(2, payment.getId());
            stmt.executeUpdate();
            payments.add(payment);
        }
    }

    public static ArrayList<Flight> search(String from, String to, DayOfWeek flightDay, Date sqlDate) throws SQLException {
    ArrayList<Flight> results = new ArrayList<>();

    String sql = """
        SELECT f.id, f.departure_airport_id, f.arrival_airport_id, f.gate,
               f.duration, f.flight_schedule_id, f.aircraft_id
        FROM flight f
        JOIN airport dep ON f.departure_airport_id = dep.id
        JOIN airport arr ON f.arrival_airport_id = arr.id
        JOIN weeklySchedule ws ON f.flight_schedule_id = ws.id
        WHERE dep.code = ? AND arr.code = ? AND ws.dayOfWeek = ?
    """;

    try (Connection conn = DbConnection.getInstance();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, from);
        stmt.setString(2, to);
        stmt.setString(3, flightDay.name());

        // Load all flight records into a buffer list
        List<Object[]> buffer = new ArrayList<>();

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Object[] data = new Object[7];
                data[0] = rs.getInt("id");
                data[1] = rs.getInt("departure_airport_id");
                data[2] = rs.getInt("arrival_airport_id");
                data[3] = rs.getString("gate");
                data[4] = rs.getInt("duration");
                data[5] = rs.getInt("flight_schedule_id");
                data[6] = rs.getInt("aircraft_id");
                buffer.add(data);
            }
        }

        // Now process after ResultSet is closed
        for (Object[] data : buffer) {
            int flightId = (int) data[0];
            int departureAirportId = (int) data[1];
            int arrivalAirportId = (int) data[2];
            String gate = (String) data[3];
            int duration = (int) data[4];
            int scheduleId = (int) data[5];
            int aircraftId = (int) data[6];

            // Now it's safe to use load methods
            Airport departure = Airport.load(departureAirportId);
            Airport arrival = Airport.load(arrivalAirportId);
            WeeklySchedule schedule = WeeklySchedule.load(scheduleId);
            Aircraft aircraft = Aircraft.load(aircraftId);

            Flight flight = new Flight(departure, arrival, gate, duration, schedule, aircraft);
            flight.setId(flightId);
            results.add(flight);
        }
    }

    return results;
}

    
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getArrivalAirportId() { return arrivalAirportId; }
    public void setArrivalAirportId(int arrivalAirportId) { this.arrivalAirportId = arrivalAirportId; }
    public int getDepartureAirportId() { return departureAirportId; }
    public void setDepartureAirportId(int departureAirportId) { this.departureAirportId = departureAirportId; }
    public String getGate() { return gate; }
    public void setGate(String gate) { this.gate = gate; }
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    public int getFlightScheduleId() { return flightScheduleId; }
    public void setFlightScheduleId(int flightScheduleId) { this.flightScheduleId = flightScheduleId; }
    public int getAircraftId() { return aircraftId; }
    public void setAircraftId(int aircraftId) { this.aircraftId = aircraftId; }
}