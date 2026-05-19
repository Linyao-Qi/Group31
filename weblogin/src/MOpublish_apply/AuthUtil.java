package com;

import jakarta.servlet.ServletContext;
import java.io.Serializable;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Utility Class for User Login Validation
 * <p>Centralized authentication manager for three roles: TA, MO, and ADMIN.
 * Loads user credentials from auth.csv and provides validation methods for login requests.
 * Must be initialized during web application startup.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
public class AuthUtil {

    /**
     * File path for authentication data CSV
     */
    private static String AUTH_FILE_PATH = "data/auth.csv";

    /**
     * In-memory credential storage for MO users
     */
    private static final Map<String, String> MO_USER_MAP = new HashMap<>();

    /**
     * In-memory credential storage for Admin users
     */
    private static final Map<String, String> ADMIN_USER_MAP = new HashMap<>();

    /**
     * In-memory credential storage for TA users
     */
    private static final Map<String, String> TA_USER_MAP = new HashMap<>();

    /**
     * Initialize authentication file path using servlet context
     * Must be invoked once during web application initialization
     * @param context ServletContext to retrieve real file path
     */
    public static void init(ServletContext context) {
        AUTH_FILE_PATH = context.getRealPath("data/auth.csv");
        loadAuthDataFromCsv();
    }

    /**
     * Load and parse user credentials from auth.csv
     * Populates MO, ADMIN, and TA credential maps
     */
    private static void loadAuthDataFromCsv() {
        List<Auth> authList = CsvFileUtil.readAuthListFromCsv(AUTH_FILE_PATH);
        for (Auth auth : authList) {
            if ("MO".equals(auth.getUserType())) {
                MO_USER_MAP.put(auth.getUserId(), auth.getPassword());
            } else if ("ADMIN".equals(auth.getUserType())) {
                ADMIN_USER_MAP.put(auth.getUserId(), auth.getPassword());
            } else if ("TA".equals(auth.getUserType())) {
                TA_USER_MAP.put(auth.getUserId(), auth.getPassword());
            }
        }
    }

    /**
     * Authenticate TA user with ID and password
     * @param taId TA user ID
     * @param password user password
     * @return true if credentials are valid, false otherwise
     */
    public static boolean authenticateTA(String taId, String password) {
        if (taId == null || taId.isBlank() || password == null || password.isBlank())
            return false;
        return password.equals(TA_USER_MAP.get(taId));
    }

    /**
     * Authenticate MO user with ID and password
     * @param moId MO user ID
     * @param password user password
     * @return true if credentials are valid, false otherwise
     */
    public static boolean authenticateMO(String moId, String password) {
        if (moId == null || moId.isBlank() || password == null || password.isBlank())
            return false;
        return password.equals(MO_USER_MAP.get(moId));
    }

    /**
     * Authenticate Admin user with ID and password
     * @param adminId Admin user ID
     * @param password user password
     * @return true if credentials are valid, false otherwise
     */
    public static boolean authenticateAdmin(String adminId, String password) {
        if (adminId == null || adminId.isBlank() || password == null || password.isBlank())
            return false;
        return password.equals(ADMIN_USER_MAP.get(adminId));
    }

    /**
     * Unified authentication method for Admin or MO
     * @param userId user ID
     * @param password user password
     * @param isAdmin true for Admin role, false for MO role
     * @return true if authenticated successfully
     */
    public static boolean authenticate(String userId, String password, boolean isAdmin) {
        return isAdmin ? authenticateAdmin(userId, password) : authenticateMO(userId, password);
    }

    /**
     * Inner Auth Entity Class
     * Stores user type, ID, and password from CSV records
     */
    public static class Auth implements Serializable {

        private static final long serialVersionUID = 1L;

        /** User type: TA, MO, or ADMIN */
        private String userType;

        /** Unique user ID */
        private String userId;

        /** User login password */
        private String password;

        /**
         * Default no-arg constructor
         */
        public Auth() {}

        /**
         * Full constructor for Auth entity
         * @param userType role type (TA/MO/ADMIN)
         * @param userId unique user ID
         * @param password login password
         */
        public Auth(String userType, String userId, String password) {
            this.userType = userType;
            this.userId = userId;
            this.password = password;
        }

        /**
         * Get user type
         * @return user role type
         */
        public String getUserType() { return userType; }

        /**
         * Set user type
         * @param userType role type to set
         */
        public void setUserType(String userType) { this.userType = userType; }

        /**
         * Get user ID
         * @return unique user ID
         */
        public String getUserId() { return userId; }

        /**
         * Set user ID
         * @param userId user ID to set
         */
        public void setUserId(String userId) { this.userId = userId; }

        /**
         * Get password
         * @return user password
         */
        public String getPassword() { return password; }

        /**
         * Set password
         * @param password password to set
         */
        public void setPassword(String password) { this.password = password; }
    }
}
