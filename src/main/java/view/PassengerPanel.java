package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.*;
import model.*;

public class PassengerPanel extends JPanel {
    private FlightBookingApp parent;
    private Flight selectedFlight;
    private int passengerCount;
    private ArrayList<PassengerForm> passengerForms;
    
    public PassengerPanel(FlightBookingApp parent) {
        this.parent = parent;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        passengerForms = new ArrayList<>();
        
        // Create components
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.NORTH);
        
        JPanel passengerFormsPanel = createPassengerFormsPanel();
        JScrollPane scrollPane = new JScrollPane(passengerFormsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(new CompoundBorder(
            new TitledBorder("Reservation Summary"),
            new EmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel flightInfoLabel = new JLabel("Flight information will appear here");
        panel.add(flightInfoLabel);
        
        return panel;
    }
    
    private JPanel createPassengerFormsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Passenger forms will be added dynamically
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton backButton = new JButton("Back to Flight Selection");
        backButton.addActionListener(e -> returnToFlightSelection());
        panel.add(backButton);
        
        // JButton saveButton = new JButton("Save Information");
        // saveButton.addActionListener(e -> savePassengerInfo());
        // panel.add(saveButton);
        
        JButton continueButton = new JButton("Continue to Seat Selection");
        continueButton.addActionListener(e -> continueToSeatSelection());
        panel.add(continueButton);
        
        return panel;
    }
    
    public void displayPassengerForms(Flight flight, int passengerCount) {
        this.selectedFlight = flight;
        this.passengerCount = passengerCount;
        
        // Update summary panel
        updateSummaryPanel();
        
        // Clear previous forms
        passengerForms.clear();
        JPanel formsContainer = getPassengerFormsContainer();
        formsContainer.removeAll();
        
        // Create a form for each passenger
        for (int i = 0; i < passengerCount; i++) {
            PassengerForm form = new PassengerForm(i + 1);
            passengerForms.add(form);
            formsContainer.add(form);
            
            // Add spacing between forms
            if (i < passengerCount - 1) {
                formsContainer.add(Box.createVerticalStrut(20));
            }
        }
        
        // Refresh UI
        formsContainer.revalidate();
        formsContainer.repaint();
    }
    
    private void updateSummaryPanel() {
        // Find the summary panel
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getBorder() != null) {
                JPanel panel = (JPanel) component;
                Border border = panel.getBorder();
                if (border instanceof CompoundBorder) {
                    Border outsideBorder = ((CompoundBorder) border).getOutsideBorder();
                    if (outsideBorder instanceof TitledBorder && 
                        ((TitledBorder) outsideBorder).getTitle().equals("Reservation Summary")) {
                        
                        // Update the summary panel
                        panel.removeAll();
                        
                        // Flight information
                        JLabel flightInfoLabel = new JLabel(String.format(
                            "Flight #%d: %s to %s", 
                            selectedFlight.getId(),
                            selectedFlight.getDepartureAirportId(),
                            selectedFlight.getArrivalAirportId()
                        ));
                        panel.add(flightInfoLabel);
                        
                        // Class and passenger count
                        JLabel classInfoLabel = new JLabel(String.format(
                            "%d Passenger%s", 
                            passengerCount,
                            passengerCount > 1 ? "s" : ""
                        ));
                        panel.add(classInfoLabel);
                        
                        panel.revalidate();
                        panel.repaint();
                        break;
                    }
                }
            }
        }
    }
    
    private JPanel getPassengerFormsContainer() {
        // Find the scroll pane
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) component;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof JPanel) {
                    return (JPanel) view;
                }
            }
        }
        
        // Should not reach here if layout is correct
        return null;
    }
    
    private void returnToFlightSelection() {
        parent.navigateTo("results"); // Return to results with current search parameters
    }
    
    private void savePassengerInfo() {
        // Validate forms
        if (!validateForms()) {
            return;
        }
        // Create passenger objects
        ArrayList<Passenger> passengers = new ArrayList<>();
        for (PassengerForm form : passengerForms) {
            Passenger passenger = new Passenger(
                form.getFullName(),
                form.getPassportNumber()
            );
            passengers.add(passenger);
        parent.setPassengers(passengers);
        JOptionPane.showMessageDialog(this, 
                "Passenger information saved successfully.", 
                "Information Saved", 
                JOptionPane.INFORMATION_MESSAGE);
        
            };
       
    }
    
    private void continueToSeatSelection() {
        // Validate forms
        if (!validateForms()) {
            return;
        }
        
        savePassengerInfo();
        
        // Navigate to seat selection
        parent.navigateTo("seatSelection");
    }
    
    private boolean validateForms() {
        for (PassengerForm form : passengerForms) {
            if (!form.validateForm()) {
                return false;
            }
        }
        return true;
    }
    
    // Inner class for passenger form
    private class PassengerForm extends JPanel {
        private int passengerNumber;
        private JTextField nameField;
        private JTextField passportField;
        private JComboBox<String> genderComboBox;
        private JSpinner ageSpinner;
        private JTextField requestsField;
        private JCheckBox frequentFlyerCheckBox;
        
        public PassengerForm(int passengerNumber) {
            this.passengerNumber = passengerNumber;
            
            setBorder(new CompoundBorder(
                new TitledBorder("Passenger #" + passengerNumber + " Information"),
                new EmptyBorder(10, 10, 10, 10)
            ));
            
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Full Name
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            add(new JLabel("Full Name:*"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            nameField = new JTextField(20);
            add(nameField, gbc);
            
            // Passport Number
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            add(new JLabel("Passport Number:*"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            passportField = new JTextField(20);
            add(passportField, gbc);
            
          
            
           
          
            
            
            // Add "Copy from Passenger 1" button for passengers after the first
            if (passengerNumber > 1) {
                gbc.gridx = 0;
                gbc.gridy = 7;
                gbc.gridwidth = 3;
                JButton copyButton = new JButton("Copy from Passenger 1");
                copyButton.addActionListener(e -> copyFromFirstPassenger());
                add(copyButton, gbc);
            }
        }
        
        
        
        private void copyFromFirstPassenger() {
            if (passengerForms.size() > 0 && passengerNumber > 1) {
                PassengerForm firstForm = passengerForms.get(0);
                nameField.setText(firstForm.nameField.getText());
                // Don't copy passport number as it should be unique
                genderComboBox.setSelectedIndex(firstForm.genderComboBox.getSelectedIndex());
                ageSpinner.setValue(firstForm.ageSpinner.getValue());
                requestsField.setText(firstForm.requestsField.getText());
                frequentFlyerCheckBox.setSelected(firstForm.frequentFlyerCheckBox.isSelected());
            }
        }
        
        public boolean validateForm() {
            // Check required fields
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter full name for Passenger #" + passengerNumber, 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                nameField.requestFocus();
                return false;
            }
            
            if (passportField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter passport number for Passenger #" + passengerNumber, 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
                passportField.requestFocus();
                return false;
            }
            
            // Check frequent flyer number if selected
          
            
            return true;
        }
        
        public String getFullName() {
            return nameField.getText().trim();
        }
        
        public String getPassportNumber() {
            return passportField.getText().trim();
        }
    }
}
