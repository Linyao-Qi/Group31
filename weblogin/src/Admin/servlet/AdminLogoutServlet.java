package Admin.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Clears the current administrator session and returns to the login page.
 *
 * @author Yutong Yao
 * @version 2.0
 */
public class AdminLogoutServlet extends HttpServlet {
    /**
     * Invalidates the current session and redirects to the shared login page.
     *
     * @param req current HTTP request
     * @param resp current HTTP response
     * @throws IOException if redirecting the response fails
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/jsp/login/login.jsp");
    }
}

