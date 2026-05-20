package com;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Servlet for MO logout requests.
 * <p>Invalidates the current session when present and redirects the user back
 * to the login page.</p>
 *
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/mo/logout")
public class MoLogoutServlet extends HttpServlet {

    /**
     * Handle logout by invalidating the current HTTP session.
     *
     * @param req HTTP request containing the optional current session
     * @param resp HTTP response used to redirect to login
     * @throws IOException if redirecting fails
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
