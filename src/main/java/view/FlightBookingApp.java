package view;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;

import model.Aircraft;
import model.Airport;
import model.Flight;
import model.FlightReservation;
import model.Passenger;
import model.Payment;
import model.PaymentMethod;
import model.Seat;
import model.User;
import model.WeeklySchedule;

/**
 * Main application frame for the Flight Booking System
 * Contains the CardLayout to switch between different panels/screens
 */
public class FlightBookingApp extends JFrame {
    // Application-wide color scheme
    public static final Color PRIMARY_COLOR = new Color(102, 0, 153); // Purple
    public static final Color ACCENT_COLOR = new Color(240, 240, 240); // Light gray
    public static final Color WHITE = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(51, 51, 51); // Dark gray
    public static final Color SUCCESS_COLOR = new Color(46, 125, 50); // Green
    public static final Color WARNING_COLOR = new Color(237, 108, 2); // Orange
    public static final Color ERROR_COLOR = new Color(211, 47, 47); // Red
    
    // Font definitions
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 20);
    public static final Font SUBHEADER_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);
    
    // Main panels
    private JPanel mainPanel;
    private CardLayout cardLayout;
    
    // User data
    private User currentUser;

    // Flight booking data
    private Flight selectedFlight;
    private Airport departureAirport;
    private Airport arrivalAirport;
    private Aircraft selectedAircraft;
    private WeeklySchedule selectedSchedule;
    private ArrayList<Passenger> passengers;
    private ArrayList<Seat> selectedSeats;
    private FlightReservation currentReservation;
    private Payment currentPayment;
    
    // References to panels
    private SearchPanel searchPanel;
    private ResultsPanel resultsPanel;
    private PassengerPanel passengerPanel;
    private SeatSelectionPanel seatSelectionPanel;
    private PaymentPanel paymentPanel;
    private ConfirmationPanel confirmationPanel;
    private FlightHistoryPanel flightHistoryPanel;
    private UserProfilePanel userProfilePanel;
    
    /**
     * Constructor - initializes the main application frame
     */
    public FlightBookingApp(User user) {
        this.currentUser = user;
        this.passengers = new ArrayList<>();
        this.selectedSeats = new ArrayList<>();
        // Initialize the main frame properties
        setTitle("SkyJourney Airlines - Flight Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(800, 600)); // Ensure minimum size
        setLocationRelativeTo(null);
        
        // Create main container with BorderLayout
        JPanel container = new JPanel(new BorderLayout());
        
        // Add header with user info and navigation
        container.add(createHeader(), BorderLayout.NORTH);
        
        // Initialize main container with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setPreferredSize(new Dimension(1200, 700));
        
        // Initialize all panels
        initializePanels();
        
        // Add the main panel to the container
        container.add(mainPanel, BorderLayout.CENTER);
        
        // Add the container to the frame
        add(container);
        
        // Display the search panel initially
        cardLayout.show(mainPanel, "search");
        
        // Make the frame visible
        setVisible(true);
        
        // Force initial UI update
        SwingUtilities.invokeLater(() -> {
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }
    
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Logo and title
        JLabel titleLabel = new JLabel("SkyJourney Airlines");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Navigation menu
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setOpaque(false);
        
        JButton homeButton = createNavButton("Home");
        homeButton.addActionListener(e -> navigateTo("search"));
        
        JButton historyButton = createNavButton("My Bookings");
        historyButton.addActionListener(e -> navigateTo("history"));
        
        JButton profileButton = createNavButton("My Profile");
        profileButton.addActionListener(e -> navigateTo("profile"));
        
        JButton logoutButton = createNavButton("Logout");
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginPage();
            }
        });
        
        navPanel.add(homeButton);
        navPanel.add(historyButton);
        navPanel.add(profileButton);
        navPanel.add(logoutButton);
        
        // User info
        JLabel userLabel = new JLabel("Welcome, " + currentUser.getUsername());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        headerPanel.add(userLabel, BorderLayout.CENTER);
        
        headerPanel.add(navPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 0, 120)); // Darker purple
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void initializePanels() {
        // Create panels
        searchPanel = new SearchPanel(this);
        resultsPanel = new ResultsPanel(this);
        passengerPanel = new PassengerPanel(this);
        seatSelectionPanel = new SeatSelectionPanel(this);
        paymentPanel = new PaymentPanel(this);
        confirmationPanel = new ConfirmationPanel(this);
        flightHistoryPanel = new FlightHistoryPanel(this);
        userProfilePanel = new UserProfilePanel(this);
        
        // Add panels to the CardLayout
        mainPanel.add(searchPanel, "search");
        mainPanel.add(resultsPanel, "results");
        mainPanel.add(passengerPanel, "passengers");
        mainPanel.add(seatSelectionPanel, "seatSelection");
        mainPanel.add(paymentPanel, "payment");
        mainPanel.add(confirmationPanel, "confirmation");
        mainPanel.add(flightHistoryPanel, "history");
        mainPanel.add(userProfilePanel, "profile");
    }
    
    public void navigateTo(String panelName) {
        System.out.println("Navigating to: " + panelName);
        System.out.println("Current passenger count before navigation: " + passengers.size());
        if ("results".equals(panelName)) {
            resultsPanel.refresh(searchPanel);
        } else if ("history".equals(panelName)) {
            flightHistoryPanel.loadUserBookings();
        } else if ("profile".equals(panelName)) {
            userProfilePanel.loadUserData();
        } else if (panelName.equals("seatSelection")) {
            System.out.println("Refreshing SeatSelectionPanel");
            seatSelectionPanel.refresh();
        }
        cardLayout.show(mainPanel, panelName);
        SwingUtilities.invokeLater(() -> {
            mainPanel.revalidate();
            mainPanel.repaint();
        });
    }
    public void resetBookingData() {
        System.out.println("data is reset");
        selectedFlight = null;
        departureAirport = null;
        arrivalAirport = null;
        selectedAircraft = null;
        selectedSchedule = null;
        passengers.clear();
        selectedSeats.clear();
        currentReservation = null;
        currentPayment = null;
    }
    
    /**
     * Create a new flight reservation based on current selections
     */
    public void createReservation() throws SQLException {
        if (selectedFlight == null || passengers.isEmpty() || selectedSeats.isEmpty()) {
            throw new IllegalStateException("Incomplete booking data");
        }
        currentReservation = new FlightReservation(selectedFlight.getId(), new java.sql.Date(System.currentTimeMillis()));
        currentReservation.setUserId(currentUser.getId());
        currentReservation.setQrCode(generateQrCode());
        currentReservation.save();
        
        for (int i = 0; i < passengers.size(); i++) {
            Passenger passenger = passengers.get(i);
            passenger.setFlightReservationId(currentReservation.getId());
            passenger.save();
            Seat seat = selectedSeats.get(i);
            currentReservation.addPassengerSeat(passenger, seat);
        }
        currentReservation.makeReservation();
    }
    
    /**
     * Process payment for the current reservation
     */
    public void processPayment(PaymentMethod method, double amount) throws SQLException {
        if (currentReservation == null) {
            throw new IllegalStateException("No reservation to pay for");
        }
        currentPayment = new Payment(amount, method, new java.sql.Date(System.currentTimeMillis()));
        currentPayment.setUserId(currentUser.getId());
        currentPayment.save();
        currentPayment.makeTransaction();
        selectedFlight.addPayment(currentPayment);
    }
    
    /**
     * Generate a simple QR code (placeholder)
     */
    private String generateQrCode() {
        return "QR_" + System.currentTimeMillis() + "_" + currentUser.getId();
    }
    
    // Getters and setters
    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User currentUser) { this.currentUser = currentUser; }
    public Flight getSelectedFlight() { return selectedFlight; }
    public void setSelectedFlight(Flight flight) { this.selectedFlight = flight; }
    public Airport getDepartureAirport() { return departureAirport; }
    public void setDepartureAirport(Airport departureAirport) { this.departureAirport = departureAirport; }
    public Airport getArrivalAirport() { return arrivalAirport; }
    public void setArrivalAirport(Airport arrivalAirport) { this.arrivalAirport = arrivalAirport; }
    public Aircraft getSelectedAircraft() { return selectedAircraft; }
    public void setSelectedAircraft(Aircraft aircraft) { this.selectedAircraft = aircraft; }
    public WeeklySchedule getSelectedSchedule() { return selectedSchedule; }
    public void setSelectedSchedule(WeeklySchedule schedule) { this.selectedSchedule = schedule; }
    public ArrayList<Passenger> getPassengers() {
        System.out.println("getPassengers() called, passenger count: " + passengers.size());
        for (int i = 0; i < passengers.size(); i++) {
            System.out.println("Passenger " + (i + 1) + ": " + passengers.get(i).getName());
        }
        return passengers;
    }    public void setPassengers(ArrayList<Passenger> passengers) { this.passengers = passengers; }
    public ArrayList<Seat> getSelectedSeats() { return selectedSeats; }
    public void setSelectedSeats(ArrayList<Seat> seats) { this.selectedSeats = seats; }
    public int getNumberOfPassengers() { return passengers.size(); }
    public FlightReservation getCurrentReservation() { return currentReservation; }
    public void setCurrentReservation(FlightReservation reservation) { this.currentReservation = reservation; }
    public Payment getCurrentPayment() { return currentPayment; }
    public void setCurrentPayment(Payment payment) { this.currentPayment = payment; }
    public CardLayout getCardLayout() { return cardLayout; }
    public JPanel getMainPanel() { return mainPanel; }
    // Getters
}
