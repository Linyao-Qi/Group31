package TA;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * TA Withdraw Servlet
 * <p>Handles withdrawal requests for teaching assistant job applications.
 * Only pending applications owned by the current TA can be withdrawn.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/withdraw")
public class TAWithdrawServlet extends HttpServlet {

    /**
     * Initializes the TA application service.
     * @throws ServletException if servlet initialization fails
     */
    @Override
    public void init() throws ServletException {
        super.init();
        TAApplicationService.init(getServletContext());
    }

    /**
     * Processes a withdrawal request and redirects back to the application status page.
     * @param req HTTP request containing appId and TA session
     * @param resp HTTP response used for redirects with result messages
     * @throws IOException if redirecting fails
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        String taId  = (String) session.getAttribute("taId");
        String appId = req.getParameter("appId");

        if (appId == null || appId.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/ta/status?"
                    + "error=" + URLEncoder.encode("Invalid request.", StandardCharsets.UTF_8));
            return;
        }

        boolean success = TAApplicationService.withdrawApplication(taId, appId);
        String param = success
                ? "msg=" + URLEncoder.encode("Application withdrawn successfully.", StandardCharsets.UTF_8)
                : "error=" + URLEncoder.encode("Cannot withdraw: only PENDING applications can be withdrawn.", StandardCharsets.UTF_8);

        resp.sendRedirect(req.getContextPath() + "/ta/status?" + param);
    }
}
