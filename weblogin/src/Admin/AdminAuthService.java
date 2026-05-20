package Admin;

/**
 * Validates administrator login credentials against the configured data source.
 *
 * @author Yutong Yao
 * @version 2.0
 */
public class AdminAuthService {
    private final AdminDataManagement dataManagement = new AdminDataManagement();

    /**
     * Checks whether the supplied administrator username and password are valid.
     *
     * @param username administrator username
     * @param password administrator password
     * @return true when the credentials match an admin account; false otherwise
     */
    public boolean validateCredentials(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }
        try {
            return dataManagement.validateAdminCredentials(username, password);
        } catch (java.io.IOException ignored) {
            return false;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

