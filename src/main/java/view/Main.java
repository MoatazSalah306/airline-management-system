package view;

import javax.swing.*;


/**
 * Main class to start the Flight Booking Application
 */
public class Main {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start application with login page
        SwingUtilities.invokeLater(() -> {
            new LoginPage();
        });
    }
}
