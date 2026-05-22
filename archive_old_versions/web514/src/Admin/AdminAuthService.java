package Admin;

public class AdminAuthService {
    private static final String FIXED_USERNAME = "admin";
    private static final String FIXED_PASSWORD = "admin123";

    public boolean validateCredentials(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            return false;
        }
        return FIXED_USERNAME.equals(username.trim()) && FIXED_PASSWORD.equals(password);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

