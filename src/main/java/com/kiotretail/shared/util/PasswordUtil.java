package com.kiotretail.shared.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt password utility for hashing and verifying passwords.
 */
public final class PasswordUtil {

    private static final int WORK_FACTOR = 12;

    private PasswordUtil() {
        // Utility class — no instances.
    }

    /**
     * Hash a plain text password using BCrypt with work factor 12.
     *
     * @param plainPassword the plain text password
     * @return the BCrypt hashed password
     */
    public static String hash(String plainPassword) {
        if (plainPassword == null) {
            throw new IllegalArgumentException("Password must not be null");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verify a plain text password against a BCrypt hash.
     *
     * @param plainPassword  the plain text password to check
     * @param hashedPassword the stored BCrypt hash
     * @return true if the password matches, false otherwise
     */
    public static boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Hash is malformed or not a valid BCrypt string.
            return false;
        }
    }

    /**
     * Check whether a string looks like a BCrypt hash (starts with $2a$ or $2b$).
     *
     * @param password the value to inspect
     * @return true if it appears to be a BCrypt hash
     */
    public static boolean isHashed(String password) {
        if (password == null || password.length() < 4) {
            return false;
        }
        return password.startsWith("$2a$") || password.startsWith("$2b$");
    }
}
