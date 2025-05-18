package view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.*;
import util.DbConnection;

/**
 * Panel for selecting seats for each passenger individually
 */
public class SeatSelectionPanel extends JPanel {
    private FlightBookingApp app;
    private JPanel seatMapPanel;
    private JPanel passengerSelectionPanel;
    private JComboBox<String> passengerSelector;
    private JLabel selectedSeatLabel;
    private JLabel instructionLabel;
    private List<Seat> availableSeats;
    private Map<Integer, JToggleButton> seatButtonMap;
    private Map<Integer, Seat> passengerSeatMap; // Maps passenger index to selected seat
    
    public SeatSelectionPanel(FlightBookingApp app) {
        this.app = app;
        this.availableSeats = new ArrayList<>();
        this.seatButtonMap = new HashMap<>();
        this.passengerSeatMap = new HashMap<>();
        
        setBackground(Color.WHITE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel with passenger selection and seat map
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Color.WHITE);
        
        // Passenger selection panel
        passengerSelectionPanel = createPassengerSelectionPanel();
        contentPanel.add(passengerSelectionPanel, BorderLayout.NORTH);
        
        // Seat map panel
        JPanel seatMapContainer = new JPanel(new BorderLayout());
        seatMapContainer.setBackground(Color.WHITE);
        seatMapContainer.setBorder(BorderFactory.createTitledBorder("Select a Seat"));
        
        seatMapPanel = new JPanel();
        seatMapPanel.setBackground(Color.WHITE);
        seatMapContainer.add(seatMapPanel, BorderLayout.CENTER);
        
        contentPanel.add(seatMapContainer, BorderLayout.CENTER);
        
        // Legend panel
        JPanel legendPanel = createLegendPanel();
        contentPanel.add(legendPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("Seat Selection");
        titleLabel.setFont(FlightBookingApp.HEADER_FONT);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JButton backButton = new JButton("← Back to Passenger Information");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setForeground(FlightBookingApp.PRIMARY_COLOR);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> app.navigateTo("passengers"));
        headerPanel.add(backButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createPassengerSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(0, 0, 15, 0)
        ));
        
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(Color.WHITE);
        
        JLabel passengerLabel = new JLabel("Select Passenger:");
        passengerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectorPanel.add(passengerLabel);
        
        passengerSelector = new JComboBox<>();
        passengerSelector.setPreferredSize(new Dimension(250, 30));
        passengerSelector.addActionListener(e -> updateSeatSelectionForPassenger());
        selectorPanel.add(passengerSelector);
        
        panel.add(selectorPanel, BorderLayout.WEST);
        
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        infoPanel.setBackground(Color.WHITE);
        
        selectedSeatLabel = new JLabel("Selected Seat: None");
        selectedSeatLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(selectedSeatLabel);
        
        panel.add(infoPanel, BorderLayout.EAST);
        
        instructionLabel = new JLabel("Please select a seat for each passenger");
        instructionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        panel.add(instructionLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Available seat
        JPanel availableItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        availableItem.setBackground(Color.WHITE);
        JButton availableButton = new JButton();
        availableButton.setPreferredSize(new Dimension(20, 20));
        availableButton.setBackground(Color.WHITE);
        availableButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        availableButton.setEnabled(false);
        availableItem.add(availableButton);
        availableItem.add(new JLabel("Available"));
        panel.add(availableItem);
        
        // Selected seat
        JPanel selectedItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        selectedItem.setBackground(Color.WHITE);
        JButton selectedButton = new JButton();
        selectedButton.setPreferredSize(new Dimension(20, 20));
        selectedButton.setBackground(FlightBookingApp.PRIMARY_COLOR);
        selectedButton.setBorder(BorderFactory.createLineBorder(FlightBookingApp.PRIMARY_COLOR));
        selectedButton.setEnabled(false);
        selectedItem.add(selectedButton);
        selectedItem.add(new JLabel("Selected"));
        panel.add(selectedItem);
        
        // Occupied seat
        JPanel occupiedItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        occupiedItem.setBackground(Color.WHITE);
        JButton occupiedButton = new JButton();
        occupiedButton.setPreferredSize(new Dimension(20, 20));
        occupiedButton.setBackground(Color.LIGHT_GRAY);
        occupiedButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        occupiedButton.setEnabled(false);
        occupiedItem.add(occupiedButton);
        occupiedItem.add(new JLabel("Occupied"));
        panel.add(occupiedItem);
        
        // Selected by another passenger
        JPanel otherPassengerItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        otherPassengerItem.setBackground(Color.WHITE);
        JButton otherPassengerButton = new JButton();
        otherPassengerButton.setPreferredSize(new Dimension(20, 20));
        otherPassengerButton.setBackground(new Color(255, 165, 0)); // Orange
        otherPassengerButton.setBorder(BorderFactory.createLineBorder(new Color(255, 165, 0)));
        otherPassengerButton.setEnabled(false);
        otherPassengerItem.add(otherPassengerButton);
        otherPassengerItem.add(new JLabel("Selected by another passenger"));
        panel.add(otherPassengerItem);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(Color.WHITE);
        
        JLabel summaryLabel = new JLabel("Seat Assignment Summary:");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(summaryLabel);
        
        panel.add(summaryPanel, BorderLayout.WEST);
        
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.setBackground(Color.WHITE);
        
        JButton confirmButton = new JButton("Confirm and Continue");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 14));
        confirmButton.setBackground(FlightBookingApp.PRIMARY_COLOR);
        confirmButton.setForeground(Color.BLACK);
        confirmButton.setFocusPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> confirmSeatSelection());
        buttonContainer.add(confirmButton);
        
        panel.add(buttonContainer, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Refresh the panel with current flight and passenger data
     */
    public void refresh() {
        try {
            System.out.println("Refreshing SeatSelectionPanel...");
            System.out.println("Selected Flight: " + (app.getSelectedFlight() != null ? app.getSelectedFlight().getId() : "null"));
            System.out.println("Selected Aircraft: " + (app.getSelectedAircraft() != null ? app.getSelectedAircraft().toString() : "null"));
            passengerSeatMap.clear();
            availableSeats = loadAvailableSeats();
            System.out.println("Available Seats: " + availableSeats.size());
            populatePassengerSelector();
            createSeatMap();
            if (passengerSelector.getItemCount() > 0) {
                System.out.println("Setting default passenger selection");
                passengerSelector.setSelectedIndex(0);
                updateSeatSelectionForPassenger();
            } else {
                System.out.println("No passengers to select");
                instructionLabel.setText("No passengers found. Please add passengers first.");
                JOptionPane.showMessageDialog(this, 
                    "No passengers found. Please add passengers in the Passenger Information section.", 
                    "No Passengers", 
                    JOptionPane.WARNING_MESSAGE);
            }
            updateSeatSummary();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error loading seats: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Load available seats for the selected flight
     */
    
    /**
     * Populate the passenger selector dropdown
     */
    private void populatePassengerSelector() {
        passengerSelector.removeAllItems();
        List<Passenger> passengers = app.getPassengers();
        System.out.println("Populating passenger selector, passenger count: " + (passengers != null ? passengers.size() : "null"));
        if (passengers != null && !passengers.isEmpty()) {
            for (int i = 0; i < passengers.size(); i++) {
                Passenger p = passengers.get(i);
                String displayName = p.getName() + " (Passenger " + (i + 1) + ")";
                System.out.println("Adding passenger to selector: " + displayName);
                passengerSelector.addItem(displayName);
            }
        } else {
            System.out.println("No passengers to populate in selector");
            instructionLabel.setText("No passengers found. Please add passengers first.");
        }
    }
    
/**
 * Main fix: This class has been enhanced to correctly display the seat map based on the Seat class structure
 * 
 * To fix the problem where no seats show as enabled despite having available seats, we need to:
 * 1. Add debug logging to understand the data format
 * 2. Handle mismatches between seat numbering formats
 * 3. Provide flexible seat lookup
 */

/**
 * Create the seat map based on aircraft configuration
 */
private void createSeatMap() {
    seatMapPanel.removeAll();
    seatButtonMap.clear();

    // Debug: Print available seats
    System.out.println("DEBUG: Creating seat map with available seats:");
    for (Seat seat : availableSeats) {
        System.out.println("Available seat ID: " + seat.getId() + 
                          ", Number: " + seat.getSeatNumber() + 
                          ", Class: " + seat.getSeatClass());
    }

    // Use FlowLayout to arrange seats
    seatMapPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

    for (Seat seat : availableSeats) {
        String seatNum = seat.getSeatNumber(); // Expected format: "A1", "B2", etc.

        System.out.println("DEBUG: Creating button for available seat: " + seatNum);

        JToggleButton seatButton = new JToggleButton(seatNum);
        seatButton.setFont(new Font("Arial", Font.PLAIN, 12));
        seatButton.setMargin(new Insets(2, 2, 2, 2));
        seatButton.setPreferredSize(new Dimension(50, 40)); // Wider for better label visibility
        seatButton.setBackground(Color.WHITE);
        seatButton.setForeground(Color.BLACK);

        // Store reference by seat ID
        seatButtonMap.put(seat.getId(), seatButton);

        // Add listener
        seatButton.addActionListener(e -> selectSeatForCurrentPassenger(seat, seatButton));

        seatMapPanel.add(seatButton);
    }

    // Refresh the panel
    seatMapPanel.revalidate();
    seatMapPanel.repaint();
}


/**
 * Get a display name for a seat (e.g., "1A", "12F")
 */
private String getSeatDisplayName(Seat seat) {
    // Since seatNumber already contains the display format, just return it
    return seat.getSeatNumber();
}

/**
 * Load available seats for the selected flight
 */
private List<Seat> loadAvailableSeats() throws SQLException {
    List<Integer> takenSeatIds = new ArrayList<>();

    // Debug the selected flight and aircraft
    System.out.println("DEBUG: Loading available seats for flight: " + 
                      (app.getSelectedFlight() != null ? app.getSelectedFlight().getId() : "null") +
                      ", aircraft: " + 
                      (app.getSelectedAircraft() != null ? app.getSelectedAircraft().getId() : "null"));

    // Step 1: Fetch taken seat IDs
    String sql = "SELECT seat_id FROM passenger_seat ps " +
                 "JOIN flightReservation fr ON ps.flightReservation_id = fr.id " +
                 "WHERE fr.flight_id = ?";
    
    try (Connection conn = DbConnection.getInstance();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, app.getSelectedFlight().getId());

        System.out.println("DEBUG: Executing SQL: " + sql.replace("?", String.valueOf(app.getSelectedFlight().getId())));
        
        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int seatId = rs.getInt("seat_id");
                takenSeatIds.add(seatId);
                System.out.println("DEBUG: Found taken seat ID: " + seatId);
            }
        }
    }

    // Step 2: Get all seats for the aircraft
    List<Seat> seats = app.getSelectedAircraft().getSeats();
    System.out.println("DEBUG: Total seats on aircraft: " + (seats != null ? seats.size() : "null"));
    
    // Step 3: Filter out taken seats
    List<Seat> available = new ArrayList<>();
    if (seats != null) {
        for (Seat seat : seats) {
            System.out.println("DEBUG: Checking seat ID: " + seat.getId() + 
                              ", Number: " + seat.getSeatNumber() + 
                              ", Is taken: " + takenSeatIds.contains(seat.getId()));
            
            if (!takenSeatIds.contains(seat.getId())) {
                available.add(seat);
                System.out.println("DEBUG: Adding available seat: " + seat.getId() + 
                                  ", Number: " + seat.getSeatNumber());
            }
        }
    }

    return available;
}
    
    /**
     * Update the seat selection UI for the currently selected passenger
     */
    private void updateSeatSelectionForPassenger() {
        int passengerIndex = passengerSelector.getSelectedIndex();
        if (passengerIndex == -1) return;
        
        // Reset all buttons to default state
        resetSeatButtonsToDefault();
        
        // Update selected seat label
        Seat selectedSeat = passengerSeatMap.get(passengerIndex);
        if (selectedSeat != null) {
            selectedSeatLabel.setText("Selected Seat: " + getSeatDisplayName(selectedSeat));
            
            // Highlight the selected seat for this passenger
            JToggleButton button = seatButtonMap.get(selectedSeat.getId());
            if (button != null) {
                button.setSelected(true);
                button.setBackground(FlightBookingApp.PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
            }
        } else {
            selectedSeatLabel.setText("Selected Seat: None");
        }
        
        // Highlight seats selected by other passengers
        for (Map.Entry<Integer, Seat> entry : passengerSeatMap.entrySet()) {
            if (entry.getKey() != passengerIndex && entry.getValue() != null) {
                JToggleButton button = seatButtonMap.get(entry.getValue().getId());
                if (button != null) {
                    button.setSelected(true);
                    button.setBackground(new Color(255, 165, 0)); // Orange
                    button.setForeground(Color.WHITE);
                    button.setEnabled(false); // Can't select another passenger's seat
                }
            }
        }
        
        // Update seat summary
        updateSeatSummary();
    }
    
    /**
     * Reset all seat buttons to their default state
     */
    private void resetSeatButtonsToDefault() {
        for (JToggleButton button : seatButtonMap.values()) {
            button.setSelected(false);
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            button.setEnabled(true);
        }
    }
    
    /**
     * Select a seat for the currently selected passenger
     */
    private void selectSeatForCurrentPassenger(Seat seat, JToggleButton button) {
        int passengerIndex = passengerSelector.getSelectedIndex();
        if (passengerIndex == -1) return;
        
        // If button is selected, assign seat to passenger
        if (button.isSelected()) {
            // Remove any previous seat assignment for this passenger
            Seat previousSeat = passengerSeatMap.get(passengerIndex);
            if (previousSeat != null) {
                JToggleButton prevButton = seatButtonMap.get(previousSeat.getId());
                if (prevButton != null && prevButton != button) {
                    prevButton.setSelected(false);
                    prevButton.setBackground(Color.WHITE);
                    prevButton.setForeground(Color.BLACK);
                }
            }
            
            // Assign new seat
            passengerSeatMap.put(passengerIndex, seat);
            button.setBackground(FlightBookingApp.PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            selectedSeatLabel.setText("Selected Seat: " + getSeatDisplayName(seat));
        } else {
            // Unassign seat
            passengerSeatMap.remove(passengerIndex);
            button.setBackground(Color.WHITE);
            button.setForeground(Color.BLACK);
            selectedSeatLabel.setText("Selected Seat: None");
        }
        
        // Update seat summary
        updateSeatSummary();
    }
    
    /**
     * Update the seat assignment summary
     */
    private void updateSeatSummary() {
        // Find the summary panel
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                JPanel panel = (JPanel) component;
                Component[] panelComponents = panel.getComponents();
                for (Component panelComponent : panelComponents) {
                    if (panelComponent instanceof JPanel && 
                        ((JPanel) panelComponent).getLayout() instanceof FlowLayout &&
                        ((FlowLayout) ((JPanel) panelComponent).getLayout()).getAlignment() == FlowLayout.LEFT) {
                        
                        JPanel summaryPanel = (JPanel) panelComponent;
                        summaryPanel.removeAll();
                        
                        JLabel summaryLabel = new JLabel("Seat Assignment Summary:");
                        summaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
                        summaryPanel.add(summaryLabel);
                        
                        // Add seat assignments
                        List<Passenger> passengers = app.getPassengers();
                        for (int i = 0; i < passengers.size(); i++) {
                            Passenger p = passengers.get(i);
                            Seat seat = passengerSeatMap.get(i);
                            String seatInfo = seat != null ? getSeatDisplayName(seat) : "Not assigned";
                            JLabel assignmentLabel = new JLabel(
                                "  •  " + p.getName() +" "+ ": " + seatInfo
                            );
                            assignmentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                            summaryPanel.add(assignmentLabel);
                        }
                        
                        summaryPanel.revalidate();
                        summaryPanel.repaint();
                        break;
                    }
                }
            }
        }
    }
    
    
    /**
     * Confirm seat selection and proceed to payment
     */
    private void confirmSeatSelection() {
        // Check if all passengers have seats assigned
        List<Passenger> passengers = app.getPassengers();
        List<Integer> unassignedPassengers = new ArrayList<>();
        
        for (int i = 0; i < passengers.size(); i++) {
            if (!passengerSeatMap.containsKey(i)) {
                unassignedPassengers.add(i + 1);
            }
        }
        
        if (!unassignedPassengers.isEmpty()) {
            StringBuilder message = new StringBuilder("Please assign seats for the following passengers:\n");
            for (int i : unassignedPassengers) {
                message.append("- Passenger ").append(i).append("\n");
            }
            
            JOptionPane.showMessageDialog(this, 
                message.toString(), 
                "Incomplete Seat Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Store selected seats in app
        ArrayList<Seat> selectedSeats = new ArrayList<>();
        for (int i = 0; i < passengers.size(); i++) {
            selectedSeats.add(passengerSeatMap.get(i));
        }
        app.setSelectedSeats(selectedSeats);
        
        // Navigate to payment page
        app.navigateTo("payment");
    }
}
