package TA;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * TA Logout Servlet.
 * <p>
 * This servlet is used to handle the logout function for teaching assistant users.
 * When a TA logs out, the current session will be cleared and the user will be
 * redirected back to the unified login page.
 * </p>
 * <p>
 * Both POST and GET requests use the same logout process.
 * </p>
 *
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/logout")
public class TALogoutServlet extends HttpServlet {

    /**
     * Handles TA logout requests sent by POST.
     * <p>
     * This method first gets the current session. If the session exists, it will
     * invalidate the session to remove the logged-in TA information. After that,
     * the user will be redirected to the login page.
     * </p>
     *
     * @param req  the HTTP request containing the current session
     * @param resp the HTTP response used to redirect the user
     * @throws IOException if an error occurs during redirection
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    /**
     * Handles TA logout requests sent by GET.
     * <p>
     * This method reuses the same logout logic as {@link #doPost(HttpServletRequest, HttpServletResponse)}
     * to make sure GET and POST requests have consistent behavior.
     * </p>
     *
     * @param req  the HTTP request
     * @param resp the HTTP response
     * @throws IOException if an error occurs during redirection
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }
}
