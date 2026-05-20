package Admin.servlet;

import Admin.AdminAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

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
            req.setAttribute("msg", "All fields are required!");
            req.getRequestDispatcher("/jsp/login/login.jsp").forward(req, resp);
            return;
        }

        if (!authService.validateCredentials(username, password)) {
            req.setAttribute("msg", "Invalid ID or Password!");
            req.getRequestDispatcher("/jsp/login/login.jsp").forward(req, resp);
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
}
