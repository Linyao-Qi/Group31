package TA;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * TA Logout Servlet
 * <p>Clears the current teaching assistant session and returns the user to the
 * TA login page. GET requests reuse the same logout behavior as POST requests.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/logout")
public class TALogoutServlet extends HttpServlet {

    /**
     * Invalidates the current TA session and redirects to the login page.
     * @param req HTTP request containing the current session
     * @param resp HTTP response used for redirecting to the login page
     * @throws IOException if the redirect fails
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        resp.sendRedirect(req.getContextPath() + "/ta/login");
    }

    /**
     * Handles browser-triggered logout links by delegating to doPost.
     * @param req HTTP request containing the current session
     * @param resp HTTP response used for redirecting to the login page
     * @throws IOException if the redirect fails
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }
}
