package TA;

import com.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/ta/login")
public class TALoginServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        TAAuthService.init(getServletContext());
    }

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
