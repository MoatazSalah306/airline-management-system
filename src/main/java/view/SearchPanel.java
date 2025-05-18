package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import model.*;
import util.DbConnection;

/**
 * Panel for searching flights based on departure/arrival airports and date
 */
public class SearchPanel extends JPanel {
    private FlightBookingApp app;
    
    // UI Components
    private JComboBox<Country> countryFromCombo;
    private JComboBox<Airport> airportFromCombo;
    private JComboBox<Country> countryToCombo;
    private JComboBox<Airport> airportToCombo;
    private JSpinner dateSpinner;
    private JSpinner passengerCountSpinner;
    private JButton searchButton;
    
    // Data Lists
    private List<Country> countries;
    private List<Airport> departureAirports;
    private List<Airport> arrivalAirports;
    
    /**
     * Constructor
     * @param app reference to the main application
     */
    public SearchPanel(FlightBookingApp app) {
        this.app = app;
        this.countries = new ArrayList<>();
        this.departureAirports = new ArrayList<>();
        this.arrivalAirports = new ArrayList<>();
        
        // Initialize data
        try {
            countries = Country.loadAll();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading country data: " + e.getMessage(), 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
        // Set panel properties
        setLayout(new BorderLayout());
        setBackground(FlightBookingApp.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create and add components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createSearchFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        
        // Initialize airport dropdowns
        updateDepartureAirports();
        updateArrivalAirports();
    }
    
    /**
     * Create the header panel with logo and title
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(FlightBookingApp.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Find Your Next Flight");
        titleLabel.setFont(FlightBookingApp.HEADER_FONT);
        titleLabel.setForeground(FlightBookingApp.PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel subtitleLabel = new JLabel("Search for flights to destinations worldwide");
        subtitleLabel.setFont(FlightBookingApp.REGULAR_FONT);
        subtitleLabel.setForeground(FlightBookingApp.TEXT_COLOR);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Create the search form with all inputs
     */
    private JPanel createSearchFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(FlightBookingApp.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // From label
        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(FlightBookingApp.SUBHEADER_FONT);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(fromLabel, gbc);
        
        // From Country dropdown
        JLabel countryFromLabel = new JLabel("Country:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(countryFromLabel, gbc);
        
        countryFromCombo = new JComboBox<>(countries.toArray(new Country[countries.size()]));
        countryFromCombo.addActionListener(e -> updateDepartureAirports());
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(countryFromCombo, gbc);
        
        // From Airport dropdown
        JLabel airportFromLabel = new JLabel("Airport:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(airportFromLabel, gbc);
        
        airportFromCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(airportFromCombo, gbc);
        
        // To label
        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(FlightBookingApp.SUBHEADER_FONT);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(toLabel, gbc);
        
        // To Country dropdown
        JLabel countryToLabel = new JLabel("Country:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(countryToLabel, gbc);
        
        countryToCombo = new JComboBox<>(countries.toArray(new Country[0]));
        countryToCombo.addActionListener(e -> updateArrivalAirports());
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(countryToCombo, gbc);
        
        // To Airport dropdown
        JLabel airportToLabel = new JLabel("Airport:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(airportToLabel, gbc);
        
        airportToCombo = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(airportToCombo, gbc);
        
        // Date
        JLabel dateLabel = new JLabel("Date:");
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(dateLabel, gbc);
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        SpinnerDateModel dateModel = new SpinnerDateModel(
            calendar.getTime(), 
            calendar.getTime(), 
            null, 
            Calendar.DAY_OF_MONTH
        );
        dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        gbc.gridy = 6;
        formPanel.add(dateSpinner, gbc);
        
        // Passengers
        JLabel passengersLabel = new JLabel("Passengers:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(passengersLabel, gbc);
        
        SpinnerNumberModel passengerModel = new SpinnerNumberModel(1, 1, 10, 1);
        passengerCountSpinner = new JSpinner(passengerModel);
        gbc.gridx = 1;
        gbc.gridy = 7;
        formPanel.add(passengerCountSpinner, gbc);
        
        
      
        
        return formPanel;
    }
    
    /**
     * Create the button panel with search button
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(FlightBookingApp.WHITE);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        searchButton = new JButton("Search Flights");
        searchButton.setBackground(FlightBookingApp.PRIMARY_COLOR);
        searchButton.setForeground(Color.black);
        searchButton.setFont(FlightBookingApp.REGULAR_FONT);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchFlights());
        
        buttonPanel.add(searchButton);
        return buttonPanel;
    }
    
    /**
     * Update departure airports based on selected country
     */
    private void updateDepartureAirports() {
        airportFromCombo.removeAllItems();
        Country selectedCountry = (Country) countryFromCombo.getSelectedItem();
        if (selectedCountry != null) {
            try {
                departureAirports = selectedCountry.getAirports();
                for (Airport airport : departureAirports) {
                    airportFromCombo.addItem(airport);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading departure airports: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Update arrival airports based on selected country
     */
    private void updateArrivalAirports() {
        airportToCombo.removeAllItems();
        Country selectedCountry = (Country) countryToCombo.getSelectedItem();
        if (selectedCountry != null) {
            try {
                arrivalAirports = selectedCountry.getAirports();
                for (Airport airport : arrivalAirports) {
                    airportToCombo.addItem(airport);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, 
                    "Error loading arrival airports: " + e.getMessage(), 
                    "Database Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Search for flights based on selected criteria
     */
    private void searchFlights() {
        Airport departureAirport = (Airport) airportFromCombo.getSelectedItem();
        Airport arrivalAirport = (Airport) airportToCombo.getSelectedItem();
        
        if (departureAirport == null || arrivalAirport == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select both departure and arrival airports.", 
                "Incomplete Information", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (departureAirport.getId() == arrivalAirport.getId()) {
            JOptionPane.showMessageDialog(this, 
                "Departure and arrival airports cannot be the same.", 
                "Invalid Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        app.setDepartureAirport(departureAirport);
        app.setArrivalAirport(arrivalAirport);
        
        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
        if (selectedDate == null) {
            throw new IllegalArgumentException("Date must be selected");
        }

        LocalDate localDate = selectedDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate();

        DayOfWeek customDay = DayOfWeek.valueOf(localDate.getDayOfWeek().name().toUpperCase());
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);

        app.setSelectedSchedule(new WeeklySchedule(customDay, null, sqlDate));
        
        int passengerCount = (Integer) passengerCountSpinner.getValue();
        app.setPassengers(new ArrayList<>(passengerCount));
        
        
        app.navigateTo("results");
    }
    
    // Getters for ResultsPanel
    public String getFrom() {
        Airport airport = (Airport) airportFromCombo.getSelectedItem();
        return airport != null ? airport.getCode() : "";
    }
    
    public String getTo() {
        Airport airport = (Airport) airportToCombo.getSelectedItem();
        return airport != null ? airport.getCode() : "";
    }
    
    public java.util.Date getDepartDate() {
        return (java.util.Date) dateSpinner.getValue();
    }
    
    public int getPassengerCount() {
        return (Integer) passengerCountSpinner.getValue();
    }
    
}
