package banking.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility class providing helper methods used across the Banking system.
 * Contains password hashing, ID generation, and input validation utilities.
 */
public final class BankingUtils {

    /** Counter used to generate sequential portions of IDs (thread-safe). */
    private static final AtomicLong COUNTER = new AtomicLong(1000);

    // Private constructor – utility class, not instantiable.
    private BankingUtils() {}

    // ─── Password Utilities ─────────────────────────────────────────────────────

    /**
     * Hashes a plain-text password using SHA-256.
     *
     * @param plainPassword The raw password string.
     * @return A hexadecimal SHA-256 hash of the password.
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(
                plainPassword.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 is always available in the JDK; this should never happen.
            throw new RuntimeException("SHA-256 algorithm not found.", e);
        }
    }

    /**
     * Verifies a plain-text password against a stored hash.
     *
     * @param plainPassword  The plain-text password to check.
     * @param hashedPassword The previously stored hash.
     * @return {@code true} if the hashes match, {@code false} otherwise.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return hashPassword(plainPassword).equals(hashedPassword);
    }

    // ─── ID Generation ──────────────────────────────────────────────────────────

    /**
     * Generates a unique User ID in the format {@code USR-<timestamp>-<counter>}.
     */
    public static String generateUserId() {
        return "USR-" + System.currentTimeMillis() % 100000
               + "-" + COUNTER.getAndIncrement();
    }

    /**
     * Generates a unique 10-digit Account Number starting with {@code 4}.
     */
    public static String generateAccountNumber() {
        long base = 4000000000L + (COUNTER.getAndIncrement() * 31L + System.currentTimeMillis() % 1000);
        return String.valueOf(Math.abs(base) % 9000000000L + 1000000000L);
    }

    /**
     * Generates a unique Transaction ID in the format {@code TXN-<timestamp>-<counter>}.
     */
    public static String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() % 1000000
               + "-" + COUNTER.getAndIncrement();
    }

    // ─── Validation Utilities ───────────────────────────────────────────────────

    /**
     * Checks whether the given string is null or blank.
     *
     * @param value The string to check.
     * @return {@code true} if null or blank.
     */
    public static boolean isNullOrBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Returns {@code true} if the given email address matches a basic pattern.
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Returns {@code true} if the given phone number consists of 10 digits.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{10}$");
    }

    /**
     * Returns {@code true} if the given password meets the minimum criteria:
     * at least 8 characters, one digit, and one special character.
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) return false;
        boolean hasDigit   = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(c ->
            "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(c) >= 0);
        return hasDigit && hasSpecial;
    }

    /**
     * Returns {@code true} if the amount is a valid positive value.
     */
    public static boolean isValidAmount(double amount) {
        return amount > 0 && !Double.isNaN(amount) && !Double.isInfinite(amount);
    }

    // ─── Display Utilities ──────────────────────────────────────────────────────

    /**
     * Masks all but the last four digits of an account number for safe display.
     * Example: {@code 1234567890} → {@code ******7890}
     */
    public static String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4)
            return "****";
        return "*".repeat(accountNumber.length() - 4)
               + accountNumber.substring(accountNumber.length() - 4);
    }

    /**
     * Formats a double amount as a currency string prefixed with ₹.
     */
    public static String formatCurrency(double amount) {
        return String.format("₹%,.2f", amount);
    }
}
