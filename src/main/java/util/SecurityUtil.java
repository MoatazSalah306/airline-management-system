package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class SecurityUtil {
    
    // Number of iterations for PBKDF2
    private static final int ITERATIONS = 10000;
    
    // Salt length in bytes
    private static final int SALT_LENGTH = 16;
    

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    

    public static String hashPassword(String password, String salt) {
        try {
            String saltedPassword = password + salt;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Apply multiple iterations for increased security
            byte[] hash = md.digest(saltedPassword.getBytes());
            for (int i = 0; i < ITERATIONS; i++) {
                md.reset();
                hash = md.digest(hash);
            }
            
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    

    public static boolean verifyPassword(String password, String storedHash, String storedSalt) {
        String computedHash = hashPassword(password, storedSalt);
        return computedHash.equals(storedHash);
    }
    

    public static String generateSecurePassword(String password) {
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return hash + ":" + salt;
    }
    

    public static boolean verifySecurePassword(String password, String storedHashSalt) {
        String[] parts = storedHashSalt.split(":");
        if (parts.length != 2) {
            return false;
        }
        
        String storedHash = parts[0];
        String storedSalt = parts[1];
        
        return verifyPassword(password, storedHash, storedSalt);
    }
}
