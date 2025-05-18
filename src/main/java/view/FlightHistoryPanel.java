package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

import model.*;

/**
 * Flight history panel for viewing user's booking history
 */
public class FlightHistoryPanel extends JPanel {
    private FlightBookingApp parent;
    private User currentUser;
    private JTable bookingsTable;
    
    public FlightHistoryPanel(FlightBookingApp app) {
        this.parent = app;
        this.currentUser = app.getCurrentUser();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
        
        // Header
        JLabel headerLabel = new JLabel("My Bookings");
        headerLabel.setFont(FlightBookingApp.HEADER_FONT);
        add(headerLabel, BorderLayout.NORTH);
        
        // Bookings table
        String[] columns = {"Booking ID", "Flight", "Date", "Status", "Payment", "Actions"};
        bookingsTable = new JTable(new Object[0][columns.length], columns);
        bookingsTable.setFillsViewportHeight(true);
        bookingsTable.setRowHeight(30);
        
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadUserBookings());
        JButton updateBtn = new JButton("Update Status");
        updateBtn.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    int reservationId = (int) bookingsTable.getValueAt(selectedRow, 0);
                    String currentStatus = bookingsTable.getValueAt(selectedRow, 3).toString();
        
                    String newStatus = JOptionPane.showInputDialog(
                        this,
                        "Enter new status (e.g., CONFIRMED, CANCELLED):",
                        currentStatus
                    );
        
                    if (newStatus != null && !newStatus.isEmpty()) {
                        // Update status in DB
                        FlightReservation reservation = FlightReservation.load(reservationId);
                        reservation.updateState(ReservationStatus.valueOf(newStatus.toUpperCase()));
                        if(ReservationStatus.valueOf(newStatus.toUpperCase()).equals(ReservationStatus.CANCELED)){
                           Payment payment = Payment.loadByReservationId(reservationId);
                           payment.setPaymentState(PaymentStatus.REFUNDED);
                           payment.update();
                        }
                        JOptionPane.showMessageDialog(this, "Status updated successfully.");
                        loadUserBookings(); // Refresh table
                    }
        
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to update status: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a booking row to update.");
            }
        });
        controlPanel.add(updateBtn);
        
        
        JButton backButton = new JButton("Back to Home");
        backButton.addActionListener(e -> parent.navigateTo("search"));
        
        controlPanel.add(backButton);
        controlPanel.add(refreshButton);
        
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    public void loadUserBookings() {
        try {
            // Get user's reservations
            ArrayList<FlightReservation> reservations = currentUser.getReservations();
            
            // Create table model
            String[] columns = {"Booking ID", "Flight", "Date", "Status", "Actions"};
            Object[][] data = new Object[reservations.size()][columns.length];
            
            // Populate data
            for (int i = 0; i < reservations.size(); i++) {
                FlightReservation reservation = reservations.get(i);
                Flight flight = Flight.load(reservation.getFlightId());
                
                // Get airports
                Airport departureAirport = Airport.load(flight.getDepartureAirportId());
                Airport arrivalAirport = Airport.load(flight.getArrivalAirportId());
                
                // Format flight info
                String flightInfo = departureAirport.getCode() + " - " + arrivalAirport.getCode();
                
                // Get schedule
                WeeklySchedule schedule = WeeklySchedule.load(flight.getFlightScheduleId());
                
                data[i][0] = reservation.getId();
                data[i][1] = flightInfo;
                data[i][2] = schedule.getDayOfWeek() + " " + schedule.getDepartureTime();
                data[i][3] = reservation.getStatus();
                
                
            }
            
            // Update table
            bookingsTable.setModel(new javax.swing.table.DefaultTableModel(data, columns));
            
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading bookings: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // public void viewBookingDetails(int bookingId) {
        // try {
        //     FlightReservation reservation = FlightReservation.load(bookingId);
        //     Flight flight = Flight.load(reservation.getFlightId());
            
        //     // Get airports
        //     Airport departureAirport = Airport.load(flight.getDepartureAirportId());
        //     Airport arrivalAirport = Airport.load(flight.getArrivalAirportId());
            
        //     // Get schedule
        //     WeeklySchedule schedule = WeeklySchedule.load(flight.getFlightScheduleId());
            
        //     // Get aircraft
        //     Aircraft aircraft = Aircraft.load(flight.getAircraftId());
            
        //     // Get passengers
        //     Map<Passenger,Seat> passengers = reservation.getPassengerSeatMap();
            
        //     // Format passenger info
        //     StringBuilder passengerInfo = new StringBuilder();
        //     for (Passenger passenger : passengers.keySet()) {
        //         passengerInfo.append("- ").append(passenger.getName());
        //         if (passenger.getPassport() != null && !passenger.getPassport().isEmpty()) {
        //             passengerInfo.append(" (Passport: ").append(passenger.getPassport()).append(")");
        //         }
        //         passengerInfo.append("\n");
        //     }
            
    //         // Show details dialog
    //         JOptionPane.showMessageDialog(this,
    //             "Booking ID: " + reservation.getId() + "\n" +
    //             "Flight: " + departureAirport.getCode() + " - " + arrivalAirport.getCode() + "\n" +
    //             "From: " + departureAirport.getName() + "\n" +
    //             "To: " + arrivalAirport.getName() + "\n" +
    //             "Schedule: " + schedule.getDayOfWeek() + " " + schedule.getDepartureTime() + "\n" +
    //             "Aircraft: " + aircraft.getModel() + "\n" +
    //             "Status: " + reservation.getStatus() + "\n" +
    //             "Booking Date: " + reservation.getBookingDate() + "\n\n" +
    //             "Passengers:\n" + passengerInfo.toString(),
    //             "Booking Details",
    //             JOptionPane.INFORMATION_MESSAGE);
                
    //     } catch (SQLException e) {
    //         e.printStackTrace();
    //         JOptionPane.showMessageDialog(this, 
    //             "Error loading booking details: " + e.getMessage(), 
    //             "Database Error", 
    //             JOptionPane.ERROR_MESSAGE);
    //     }
    // }
    
    // Custom button renderer for the table
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }
    
    // Custom button editor for the table
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private FlightHistoryPanel panel;
        
        public ButtonEditor(JCheckBox checkBox, FlightHistoryPanel panel) {
            super(checkBox);
            this.panel = panel;
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }
        
    
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}
