package Admin.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Utility that protects administrator web pages from unauthenticated access.
 *
 * @author Yutong Yao
 * @version 2.0
 */
public final class AdminWebAuthGuard {
    private AdminWebAuthGuard() {
    }

    /**
     * Ensures the current request belongs to an authenticated admin session.
     *
     * @param req current HTTP request
     * @param resp current HTTP response
     * @return true when the user is authenticated; false after redirecting to login
     * @throws IOException if the redirect cannot be written
     */
    public static boolean ensureAuthenticated(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null && AdminWebSessionState.isAuthenticated(session)) {
            return true;
        }
        resp.sendRedirect(req.getContextPath() + "/jsp/login/login.jsp");
        return false;
    }
}

