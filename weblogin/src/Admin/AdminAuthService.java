package Admin;

public class AdminAuthService {
    private final AdminDataManagement dataManagement = new AdminDataManagement();

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

