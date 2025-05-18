package view;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Time;

import model.*;

public class ConfirmationPanel extends JPanel {
    private FlightBookingApp app;

    public ConfirmationPanel(FlightBookingApp app) {
        this.app = app;
        setBackground(FlightBookingApp.ACCENT_COLOR);
        setLayout(new BorderLayout());
        
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(FlightBookingApp.ACCENT_COLOR);
        
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(FlightBookingApp.ACCENT_COLOR);
        
        JLabel headerLabel = new JLabel("BOOKING CONFIRMATION");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(FlightBookingApp.TEXT_COLOR);
        
        headerPanel.add(headerLabel);
        
        JTextArea confirmationText = new JTextArea();
        confirmationText.setFont(new Font("Arial", Font.PLAIN, 14));
        confirmationText.setEditable(false);
        confirmationText.setBackground(FlightBookingApp.ACCENT_COLOR);
        confirmationText.setText(generateConfirmationText());
        
        JButton newBookingButton = new JButton("New Booking");
        newBookingButton.setFont(new Font("Arial", Font.BOLD, 14));
        newBookingButton.setBackground(FlightBookingApp.PRIMARY_COLOR);
        newBookingButton.setForeground(FlightBookingApp.WHITE);
        newBookingButton.setFocusPainted(false);
        newBookingButton.setBorderPainted(false);
        newBookingButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        newBookingButton.setPreferredSize(new Dimension(120, 40));
        newBookingButton.addActionListener(e -> {
            app.resetBookingData();
            app.navigateTo("search");
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(FlightBookingApp.ACCENT_COLOR);
        buttonPanel.add(newBookingButton);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(confirmationText), BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private String generateConfirmationText() {
        StringBuilder confirmation = new StringBuilder("Booking Confirmation\n\n");
        try {
            Flight flight = app.getSelectedFlight();
            if (flight == null) {
                return "Error: No flight selected. Please start a new booking.\n";
            }
            Airport depAirport = Airport.load(flight.getDepartureAirportId());
            Airport arrAirport = Airport.load(flight.getArrivalAirportId());
            WeeklySchedule schedule = WeeklySchedule.load(flight.getFlightScheduleId());
            Aircraft aircraft = app.getSelectedAircraft();
            if (aircraft == null) {
                return "Error: Aircraft information missing. Please start a new booking.\n";
            }
            Airline airline = Airline.load(aircraft.getAirlineId());
            FlightReservation reservation = app.getCurrentReservation();
            if (reservation == null) {
                return "Error: Reservation not found. Please start a new booking.\n";
            }
            
            confirmation.append("Reservation ID: ").append(reservation.getId()).append("\n")
                        .append("QR Code: ").append(reservation.getQrCode()).append("\n\n")
                        .append("Flight Details:\n")
                        .append("Airline: ").append(airline.getName()).append("\n")
                        .append("From: ").append(depAirport.getName()).append(" (").append(depAirport.getCode()).append(")\n")
                        .append("To: ").append(arrAirport.getName()).append(" (").append(arrAirport.getCode()).append(")\n")
                        .append("Departure: ").append(schedule.getDepartureTime()).append("\n")
                        .append("Arrival: ").append(calculateArrivalTime(schedule.getDepartureTime(), flight.getDuration())).append("\n")
                        .append("Duration: ").append(flight.getDuration() != null ? flight.getDuration() / 60 + "h " + flight.getDuration() % 60 + "m" : "N/A").append("\n")
                        .append("Gate: ").append(flight.getGate()).append("\n\n")
                        .append("Passengers and Seats:\n");
            
            for (Passenger passenger : app.getPassengers()) {
                Seat seat = reservation.getPassengerSeatMap().get(passenger);
                confirmation.append("Passenger: ").append(passenger.getName()).append(" (Passport: ").append(passenger.getPassport()).append(")\n")
                            .append("Seat: ").append(seat.getId()).append(" (").append(seat.getSeatClass()).append(")\n");
            }
            
            Payment payment = app.getCurrentPayment();
            confirmation.append("\nPayment Details:\n")
                        .append("Amount: $").append(String.format("%.2f", payment.getPaymentAmount())).append("\n")
                        .append("Method: ").append(payment.getPaymentMethod()).append("\n")
                        .append("Status: ").append(payment.getPaymentState());
        } catch (SQLException e) {
            confirmation.append("Error loading confirmation details: ").append(e.getMessage());
        }
        return confirmation.toString();
    }
    
    private String calculateArrivalTime(Time departureTime, Integer duration) {
        if (duration == null) return "N/A";
        long depMillis = departureTime.getTime();
        long arrMillis = depMillis + (duration * 60 * 1000);
        return new Time(arrMillis).toString();
    }
}
