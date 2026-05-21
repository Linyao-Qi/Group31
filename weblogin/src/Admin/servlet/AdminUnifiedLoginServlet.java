package Admin.servlet;

import Admin.AdminAuthService;
import com.AuthUtil;
import com.CsvFileUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Authenticates administrators from the shared system login page.
 *
 * @author Yutong Yao
 * @version 2.0
 */
public class AdminUnifiedLoginServlet extends HttpServlet {
    private final AdminAuthService authService = new AdminAuthService();

    /**
     * Validates shared-login credentials and initializes admin session attributes.
     *
     * @param req current HTTP request
     * @param resp current HTTP response
     * @throws ServletException if forwarding to the JSP fails
     * @throws IOException if the response cannot be written
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = safe(req.getParameter("userId"));
        String password = req.getParameter("password");
        if (password == null) {
            password = "";
        }

        if (username.isEmpty() || password.isEmpty()) {
            forwardWithAdminState(req, resp, "All fields are required!", username);
            return;
        }

        if (!authService.validateCredentials(username, password)) {
            forwardWithAdminState(req, resp, "Invalid ID or Password!", username);
            return;
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("userType", "ADMIN");
        session.setAttribute("userId", username);
        AdminWebSessionState.setAuthenticated(session, username);
        resp.sendRedirect(req.getContextPath() + "/admin/home");
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private void forwardWithAdminState(HttpServletRequest req, HttpServletResponse resp,
                                       String message, String username) throws ServletException, IOException {
        req.setAttribute("msg", message);
        req.setAttribute("selectedUserType", "ADMIN");
        req.setAttribute("userIdValue", username);
        req.setAttribute("nextTaId", getNextTaId(req));
        req.getRequestDispatcher("/jsp/login/login.jsp").forward(req, resp);
    }

    private String getNextTaId(HttpServletRequest req) {
        String authFilePath = req.getServletContext().getRealPath("data/auth.csv");
        List<AuthUtil.Auth> authList = CsvFileUtil.readAuthListFromCsv(authFilePath);
        int maxNumber = 0;
        int width = 3;
        for (AuthUtil.Auth auth : authList) {
            if (auth.getUserType() == null || !"TA".equalsIgnoreCase(auth.getUserType())) {
                continue;
            }
            String userId = safe(auth.getUserId()).toUpperCase();
            if (!userId.matches("TA\\d+")) {
                continue;
            }
            String numberPart = userId.substring(2);
            int number = Integer.parseInt(numberPart);
            if (number > maxNumber) {
                maxNumber = number;
                width = Math.max(3, numberPart.length());
            }
        }
        return "TA" + String.format("%0" + width + "d", maxNumber + 1);
    }
}
