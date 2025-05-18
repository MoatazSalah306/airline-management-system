package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import model.*;

/**
 * Payment panel for processing flight booking payments
 */
public class PaymentPanel extends JPanel {
    private FlightBookingApp app;
    
    // Form fields
    private JTextField cardNumberField;
    private JTextField nameOnCardField;
    private JComboBox<String> expiryMonthCombo;
    private JComboBox<String> expiryYearCombo;
    private JTextField cvvField;
    private JComboBox<PaymentMethod> paymentMethodCombo;
    private JLabel totalAmountLabel;
    private double totalAmount;
    
    public PaymentPanel(FlightBookingApp app) {
        this.app = app;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        
        JLabel headerLabel = new JLabel("Payment Information");
        headerLabel.setFont(FlightBookingApp.HEADER_FONT);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        
        JButton backButton = new JButton("← Back to Seat Selection");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setForeground(FlightBookingApp.PRIMARY_COLOR);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(_ -> app.navigateTo("seatSelection"));
        headerPanel.add(backButton, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        // Payment form
        JPanel formPanel = createPaymentForm();
        contentPanel.add(formPanel, BorderLayout.CENTER);
        
        // Order summary
        JPanel summaryPanel = createOrderSummary();
        contentPanel.add(summaryPanel, BorderLayout.EAST);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton payButton = new JButton("Complete Payment");
        payButton.setBackground(FlightBookingApp.PRIMARY_COLOR);
        payButton.setForeground(Color.black);
        payButton.setFont(new Font("Arial", Font.BOLD, 14));
        payButton.setFocusPainted(false);
        payButton.addActionListener(e -> processPayment());
        
        buttonPanel.add(payButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createPaymentForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Payment Details"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Payment method
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Payment Method:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        paymentMethodCombo = new JComboBox<>(PaymentMethod.values());
        panel.add(paymentMethodCombo, gbc);
        
        // Card number
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Card Number:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        cardNumberField = new JTextField(16);
        panel.add(cardNumberField, gbc);
        
        // Name on card
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Name on Card:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        nameOnCardField = new JTextField(20);
        panel.add(nameOnCardField, gbc);
        
        // Expiry date
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Expiry Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        expiryMonthCombo = new JComboBox<>(months);
        panel.add(expiryMonthCombo, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        String[] years = new String[10];
        int currentYear = java.time.Year.now().getValue();
        for (int i = 0; i < 10; i++) {
            years[i] = String.valueOf(currentYear + i);
        }
        expiryYearCombo = new JComboBox<>(years);
        panel.add(expiryYearCombo, gbc);
        
        // CVV
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("CVV:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        cvvField = new JTextField(3);
        panel.add(cvvField, gbc);
        
        // Billing address section
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        panel.add(new JSeparator(), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        JLabel billingLabel = new JLabel("Billing Address");
        billingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(billingLabel, gbc);
        
        // Address fields
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Address Line 1:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(new JTextField(20), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Address Line 2:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(new JTextField(20), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        panel.add(new JLabel("City:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        panel.add(new JTextField(20), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        panel.add(new JLabel("State/Province:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        panel.add(new JTextField(20), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Postal Code:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        panel.add(new JTextField(10), gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Country:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        String[] countries = {"United States", "Canada", "United Kingdom", "Australia", "Germany", "France", "Japan"};
        panel.add(new JComboBox<>(countries), gbc);
        
        return panel;
    }
    
    private JPanel createOrderSummary() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Order Summary"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(300, 400));
        
        try {
            Flight flight = app.getSelectedFlight();
            if (flight != null) {
                // Flight details
                Airport depAirport = Airport.load(flight.getDepartureAirportId());
                Airport arrAirport = Airport.load(flight.getArrivalAirportId());
                
                JLabel flightLabel = new JLabel("Flight: " + depAirport.getCode() + " to " + arrAirport.getCode());
                flightLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(flightLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                
                // Passenger count
                int passengerCount = app.getPassengers().size();
                JLabel passengersLabel = new JLabel("Passengers: " + passengerCount);
                passengersLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(passengersLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                
                // Seat class
                JLabel classLabel = new JLabel("Class: " + app.getSelectedSeats().get(0).getSeatClass());
                classLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(classLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                
                // Seat numbers
                StringBuilder seatNumbers = new StringBuilder();
                for (Seat seat : app.getSelectedSeats()) {
                    if (seatNumbers.length() > 0) {
                        seatNumbers.append(", ");
                    }
                    seatNumbers.append(seat.getId());
                }
                
                JLabel seatsLabel = new JLabel("Seats: " + seatNumbers.toString());
                seatsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(seatsLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                // Add separator
                JSeparator separator = new JSeparator();
                separator.setAlignmentX(Component.LEFT_ALIGNMENT);
                separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                panel.add(separator);
                panel.add(Box.createRigidArea(new Dimension(0, 15)));
                
                // Price calculation
                double basePrice = 250.0;
                double classMultiplier = 1.0;
                switch (app.getSelectedSeats().get(0).getSeatClass()) {
                    case SeatClass.Economy:
                        classMultiplier = 1.0;
                        break;
                    case SeatClass.Business:
                        classMultiplier = 2.0;
                        break;
                    case SeatClass.FirstClass:
                        classMultiplier = 3.0;
                        break;
                }
                
                double subtotal = basePrice * classMultiplier * passengerCount;
                double taxes = subtotal * 0.1; // 10% tax
                double total = subtotal + taxes;
                
                // Display prices
                JLabel subtotalLabel = new JLabel(String.format("Subtotal: $%.2f", subtotal));
                subtotalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(subtotalLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                
                JLabel taxesLabel = new JLabel(String.format("Taxes & Fees: $%.2f", taxes));
                taxesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(taxesLabel);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                
                // Add another separator
                JSeparator separator2 = new JSeparator();
                separator2.setAlignmentX(Component.LEFT_ALIGNMENT);
                separator2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                panel.add(separator2);
                panel.add(Box.createRigidArea(new Dimension(0, 5)));
                
                // Total
                totalAmountLabel = new JLabel(String.format("Total: $%.2f", total));
                totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
                totalAmountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                panel.add(totalAmountLabel);
                
                // Store total for payment processing
                totalAmount=total;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading order summary");
            errorLabel.setForeground(Color.RED);
            panel.add(errorLabel);
        }
        
        return panel;
    }
    
    private void processPayment() {
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        try {
            // Create reservation
            FlightReservation reservation = new FlightReservation(
                app.getSelectedFlight().getId(),
                new java.sql.Date(System.currentTimeMillis())
            );
            reservation.setUserId(app.getCurrentUser().getId());
            
            // Save reservation
            reservation.save();
            
            // Add passengers and seats
            for (int i = 0; i < app.getPassengers().size(); i++) {
                Passenger passenger = app.getPassengers().get(i);
                Seat seat = app.getSelectedSeats().get(i);
                
                // Save passenger
                passenger.save();
                
                // Associate passenger with reservation
                reservation.addPassengerSeat(passenger, seat);
                
            }
            
            // Create payment
            Payment payment = new Payment(totalAmount,(PaymentMethod) paymentMethodCombo.getSelectedItem(),new java.sql.Date(System.currentTimeMillis()));
            payment.setUserId(app.getCurrentUser().getId());
            payment.setPaymentState(PaymentStatus.COMPLETED);
            payment.setPaymentAmount(totalAmount);
            // Save payment
            payment.save();
            
            // Store in app for confirmation page
            app.setCurrentReservation(reservation);
            app.setCurrentPayment(payment);
            app.createReservation();
            // Show success message
            JOptionPane.showMessageDialog(this,
                "Payment processed successfully!",
                "Payment Success",
                JOptionPane.INFORMATION_MESSAGE);
                
            // Navigate to confirmation page
            app.navigateTo("search");
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error processing payment: " + e.getMessage(),
                "Payment Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        // Card number validation
        String cardNumber = cardNumberField.getText().trim();
        if (cardNumber.isEmpty()) {
            showError("Please enter card number");
            return false;
        }
        
        if (!cardNumber.matches("\\d{16}")) {
            showError("Card number must be 16 digits");
            return false;
        }
        
        // Name validation
        String nameOnCard = nameOnCardField.getText().trim();
        if (nameOnCard.isEmpty()) {
            showError("Please enter name on card");
            return false;
        }
        
        // CVV validation
        String cvv = cvvField.getText().trim();
        if (cvv.isEmpty()) {
            showError("Please enter CVV");
            return false;
        }
        
        if (!cvv.matches("\\d{3}")) {
            showError("CVV must be 3 digits");
            return false;
        }
        
        return true;
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
            message,
            "Validation Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
