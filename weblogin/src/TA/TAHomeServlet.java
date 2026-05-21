package TA;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * TA Home Servlet
 * <p>Controls access to the teaching assistant home page. The servlet checks
 * whether a TA session exists before forwarding to the TA dashboard.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/home")
public class TAHomeServlet extends HttpServlet {

    /**
     * Handles TA home page requests.
     * @param req HTTP request containing the TA session
     * @param resp HTTP response used for redirecting or forwarding
     * @throws ServletException if request forwarding fails
     * @throws IOException if redirecting or forwarding fails
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }
        req.getRequestDispatcher("/jsp/TA/home.jsp").forward(req, resp);
    }
}
