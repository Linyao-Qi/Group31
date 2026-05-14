package Admin.servlet;

import Admin.AdminAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 登录控制器。
 * 负责展示登录页、校验账号并建立登录会话。
 */
public class AdminLoginServlet extends HttpServlet {
    private final AdminAuthService authService = new AdminAuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && AdminWebSessionState.isAuthenticated(session)) {
            resp.sendRedirect(req.getContextPath() + "/admin/home");
            return;
        }
        req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = safe(req.getParameter("username"));
        String password = req.getParameter("password");
        if (password == null) {
            password = "";
        }

        if (username.isEmpty() || password.isEmpty()) {
            req.setAttribute("error", "Please input both username and password.");
            req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req, resp);
            return;
        }

        if (!authService.validateCredentials(username, password)) {
            req.setAttribute("error", "Invalid username or password.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/jsp/admin/login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(true);
        AdminWebSessionState.setAuthenticated(session, username);
        resp.sendRedirect(req.getContextPath() + "/admin/home");
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}

