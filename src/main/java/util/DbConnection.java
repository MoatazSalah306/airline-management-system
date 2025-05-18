package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility class with improved connection management
 */
public class DbConnection {

    private static DbConnection instance = null;
    private Connection conn = null;
    
    // Database configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/flights";
    private static final String USER = "root";
    private static final String PASS = "1234";
    
    // Maximum number of connection attempts
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    // Private constructor to prevent direct instantiation
    private DbConnection() {}

    /**
     * Initialize the database connection
     * @throws SQLException if connection fails after retry attempts
     */
    private void init() throws SQLException {
        int attempts = 0;
        SQLException lastException = null;
        
        // Try to connect with retry logic
        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                System.out.println("Connected to database successfully");
                return;
            } catch (SQLException e) {
                lastException = e;
                attempts++;
                System.err.println("Connection attempt " + attempts + " failed: " + e.getMessage());
                
                // Wait before retrying
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        // If we get here, all attempts failed
        throw new SQLException("Failed to connect to database after " + MAX_RETRY_ATTEMPTS + " attempts", lastException);
    }

    /**
     * Get the current connection
     * @return The database connection
     */
    public Connection getConnection() {
        return conn;
    }

    /**
     * Get a database connection instance
     * @return A valid database connection
     * @throws SQLException if connection cannot be established
     */
    public static Connection getInstance() throws SQLException {
        if (instance == null || instance.getConnection() == null || instance.getConnection().isClosed()) {
            instance = new DbConnection();
            instance.init();
        }
        return instance.getConnection();
    }
    
    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (instance != null && instance.conn != null) {
            try {
                instance.conn.close();
                System.out.println("Database connection closed");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
