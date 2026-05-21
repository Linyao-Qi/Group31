package TA;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

<<<<<<< Updated upstream
=======
/**
 * TA Logout Servlet
 * <p>Clears the current teaching assistant session and returns the user to the
 * unified login page. GET requests reuse the same logout behavior as POST requests.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
>>>>>>> Stashed changes
@WebServlet("/ta/logout")
public class TALogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) session.invalidate();
        resp.sendRedirect(req.getContextPath() + "/jsp/login/login.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }
}
