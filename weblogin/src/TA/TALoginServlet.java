package TA;

import com.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * TA Login Servlet
 * <p>Handles teaching assistant login requests. GET requests display the login
 * page, while POST requests validate credentials and create a TA session.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/login")
public class TALoginServlet extends HttpServlet {

    /**
     * Initializes authentication services for TA login.
     * @throws ServletException if servlet initialization fails
     */
    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        TAAuthService.init(getServletContext());
    }

    /**
     * Displays the TA login page or redirects an already logged-in TA home.
     * @param req HTTP request containing optional session data
     * @param resp HTTP response used for redirecting or forwarding
     * @throws ServletException if request forwarding fails
     * @throws IOException if redirecting or forwarding fails
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("taId") != null) {
            resp.sendRedirect(req.getContextPath() + "/ta/home");
            return;
        }
        req.getRequestDispatcher("/jsp/TA/login.jsp").forward(req, resp);
    }

    /**
     * Validates TA login credentials and stores taId in the session on success.
     * @param req HTTP request containing taId and password
     * @param resp HTTP response used for redirecting or forwarding
     * @throws ServletException if request forwarding fails
     * @throws IOException if redirecting or forwarding fails
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String taId = req.getParameter("taId");
        String password = req.getParameter("password");

        if (TAAuthService.authenticateTA(taId, password)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("taId", taId);
            resp.sendRedirect(req.getContextPath() + "/ta/home");
        } else {
            req.setAttribute("error", "Invalid TA ID or password.");
            req.setAttribute("taIdValue", taId);
            req.getRequestDispatcher("/jsp/TA/login.jsp").forward(req, resp);
        }
    }
}
