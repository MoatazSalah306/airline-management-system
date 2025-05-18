package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import model.*;

/**
 * Results panel for displaying flight search results
 */
public class ResultsPanel extends JPanel {
    private FlightBookingApp parent;
    private JPanel resultsContainer;
    private ArrayList<Flight> flights;
    private SearchPanel searchPanel;
    
    public ResultsPanel(FlightBookingApp app) {
        this.parent = app;
        this.flights = new ArrayList<>();
    
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);
    
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
    
        JLabel headerLabel = new JLabel("Flight Search Results");
        headerLabel.setFont(FlightBookingApp.HEADER_FONT);
        headerPanel.add(headerLabel, BorderLayout.WEST);
    
        JButton backButton = new JButton("New Search");
        backButton.addActionListener(e -> parent.navigateTo("search"));
        headerPanel.add(backButton, BorderLayout.EAST);
    
        add(headerPanel, BorderLayout.NORTH);
    
        // =====================
        // Content Section (Summary + ScrollPane or No Results)
        // =====================
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
    
        // Summary Panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
    
        JLabel summaryLabel = new JLabel("Search summary will appear here");
        summaryPanel.add(summaryLabel);
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
    
        // Results Container
        resultsContainer = new JPanel();
        resultsContainer.setLayout(new BoxLayout(resultsContainer, BoxLayout.Y_AXIS));
        resultsContainer.setBackground(Color.WHITE);
    
        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(resultsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    
        // No Results Label (initially hidden)
        JLabel noResultsLabel = new JLabel("No flights found. Please try different search criteria.");
        noResultsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        noResultsLabel.setHorizontalAlignment(JLabel.CENTER);
        noResultsLabel.setVisible(false);
        contentPanel.add(noResultsLabel, BorderLayout.SOUTH);
    
        // Add content panel to center
        add(contentPanel, BorderLayout.CENTER);
    }
    
    
    public void refresh(SearchPanel searchPanel) {
        this.searchPanel = searchPanel;
        
        // Update search summary
        updateSearchSummary();
        
        // Clear previous results
        resultsContainer.removeAll();
        
        try {
            // Get flights based on search criteria
            flights = searchFlights(
                searchPanel.getFrom(),
                searchPanel.getTo(),
                searchPanel.getDepartDate()
            );
            
            if (flights.isEmpty()) {
                // Show no results message
                JLabel noResultsLabel = new JLabel("No flights found. Please try different search criteria.");
                noResultsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                noResultsLabel.setHorizontalAlignment(JLabel.CENTER);
                noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                resultsContainer.add(Box.createVerticalGlue());
                resultsContainer.add(noResultsLabel);
                resultsContainer.add(Box.createVerticalGlue());
            } else {
                // Add flight cards
                for (Flight flight : flights) {
                    System.out.print(flight.getGate());
                    resultsContainer.add(createFlightCard(flight));
                    resultsContainer.add(Box.createRigidArea(new Dimension(0, 10)));
                }
            }
            
            // Refresh UI
            resultsContainer.revalidate();
            resultsContainer.repaint();
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error searching flights: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateSearchSummary() {
        // Find the summary panel
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && 
                ((JPanel) component).getBorder() instanceof javax.swing.border.MatteBorder) {
                
                JPanel panel = (JPanel) component;
                panel.removeAll();
                
                // Format date
                java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("EEE, MMM d, yyyy");
                String formattedDate = dateFormat.format(searchPanel.getDepartDate());
                
                // Create summary text
                JLabel summaryLabel = new JLabel(String.format(
                    "From: %s | To: %s | Date: %s | Passengers: %d | Class: %s",
                    searchPanel.getFrom(),
                    searchPanel.getTo(),
                    formattedDate,
                    searchPanel.getPassengerCount()
                ));
                summaryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                
                panel.add(summaryLabel);
                panel.revalidate();
                panel.repaint();
                break;
            }
        }
    }
    
    private ArrayList<Flight> searchFlights(String from, String to, java.util.Date date) throws SQLException {
        // Convert date to SQL date
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        
        // Get day of week
        // int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
        LocalDate localDate = sqlDate.toLocalDate();
        int dayOfWeek = localDate.getDayOfWeek().getValue();
        // Convert to our DayOfWeek enum (1=Sunday in Calendar, but we need 0=SUNDAY in our enum)
        DayOfWeek flightDay = DayOfWeek.values()[dayOfWeek];
        
        // Search flights
        System.out.println("start searching from code is "+from+"to code is "+to+"day is "+flightDay.name());
        
        return Flight.search(from, to, flightDay, sqlDate);
    }
    
    private JPanel createFlightCard(Flight flight) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        try {
            // Get related data
            Airport departureAirport = Airport.load(flight.getDepartureAirportId());
            Airport arrivalAirport = Airport.load(flight.getArrivalAirportId());
            Aircraft aircraft = Aircraft.load(flight.getAircraftId());
            Airline airline = Airline.load(aircraft.getAirlineId());
            WeeklySchedule schedule = WeeklySchedule.load(flight.getFlightScheduleId());
            
            // Flight info panel
            JPanel infoPanel = new JPanel(new GridLayout(0, 1));
            infoPanel.setBackground(Color.WHITE);
            
            // Airline and flight number
            JLabel airlineLabel = new JLabel(airline.getName() + " - Flight #" + flight.getId());
            airlineLabel.setFont(new Font("Arial", Font.BOLD, 14));
            infoPanel.add(airlineLabel);
            
            // Route
            JLabel routeLabel = new JLabel(departureAirport.getCode() + " → " + arrivalAirport.getCode());
            routeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            infoPanel.add(routeLabel);
            
            // Airports
            JLabel airportsLabel = new JLabel(departureAirport.getName() + " to " + arrivalAirport.getName());
            airportsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            infoPanel.add(airportsLabel);
            
            // Schedule
            JLabel scheduleLabel = new JLabel("Departure: " + schedule.getDepartureTime());
            scheduleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            infoPanel.add(scheduleLabel);
            
            // Duration
            String duration = flight.getDuration() != null ? 
                (flight.getDuration() / 60) + "h " + (flight.getDuration() % 60) + "m" : 
                "N/A";
            JLabel durationLabel = new JLabel("Duration: " + duration);
            durationLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            infoPanel.add(durationLabel);
            
            // Aircraft
            JLabel aircraftLabel = new JLabel("Aircraft: " + aircraft.getModel());
            aircraftLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            infoPanel.add(aircraftLabel);
            
            card.add(infoPanel, BorderLayout.CENTER);
            
            // Price and select button panel
            JPanel actionPanel = new JPanel(new BorderLayout());
            actionPanel.setBackground(Color.WHITE);
            
            // Price (would normally come from database)
            double basePrice = 250.0;
            double price = basePrice * (1 + (Math.random() * 0.5)); // Random variation
            
            JLabel priceLabel = new JLabel(String.format("$%.2f", price));
            priceLabel.setFont(new Font("Arial", Font.BOLD, 18));
            actionPanel.add(priceLabel, BorderLayout.NORTH);
            
            // Select button
            JButton selectButton = new JButton("Select");
            selectButton.setBackground(FlightBookingApp.PRIMARY_COLOR);
            selectButton.setForeground(Color.BLACK);
            selectButton.setFocusPainted(false);
            selectButton.addActionListener(e -> selectFlight(flight, aircraft));
            actionPanel.add(selectButton, BorderLayout.SOUTH);
            
            card.add(actionPanel, BorderLayout.EAST);
            
        } catch (SQLException e) {
            e.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading flight details");
            errorLabel.setForeground(Color.RED);
            card.add(errorLabel, BorderLayout.CENTER);
        }
        
        return card;
    }
    
    private void selectFlight(Flight flight, Aircraft aircraft) {
        // Store selected flight and passenger count in parent app
        parent.setSelectedFlight(flight);
        parent.setSelectedAircraft(aircraft);
        
        // Create empty passenger list based on count
        int passengerCount = searchPanel.getPassengerCount();
        ArrayList<Passenger> passengers = new ArrayList<>(passengerCount);
        parent.setPassengers(passengers);
        
        // Navigate to passenger info panel
        parent.navigateTo("passengers");
        
        // Initialize passenger forms
        Component[] components = parent.getMainPanel().getComponents();
        for (Component component : components) {
            if (component instanceof PassengerPanel) {
                ((PassengerPanel) component).displayPassengerForms(
                    flight, 
                    passengerCount
                );
                break;
            }
        }
    }
}
