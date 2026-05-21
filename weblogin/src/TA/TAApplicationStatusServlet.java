package TA;

import com.Application;
import com.AuthUtil;
import com.Job;
import com.MoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TA Application Status Servlet
 * <p>Displays all applications submitted by the logged-in teaching assistant.
 * It also loads related job data so the JSP can show job details beside each
 * application status.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/status")
public class TAApplicationStatusServlet extends HttpServlet {

    /** Service used to load job details for application display */
    private final MoService moService = new MoService();

    /**
     * Initializes authentication, job, and application services.
     * @throws ServletException if servlet initialization fails
     */
    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
        TAApplicationService.init(getServletContext());
    }

    /**
     * Loads the current TA's application records and forwards them to the status page.
     * @param req HTTP request containing the TA session and optional message parameters
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

        String taId = (String) session.getAttribute("taId");
        List<Application> apps = TAApplicationService.getApplicationsByTA(taId);
        req.setAttribute("apps", apps);

        // Build jobId -> Job map for display.
        Map<String, Job> jobMap = new HashMap<>();
        for (Job job : moService.getAllJobs()) {
            jobMap.put(job.getJobId(), job);
        }
        req.setAttribute("jobMap", jobMap);

        String msg   = req.getParameter("msg");
        String error = req.getParameter("error");
        if (msg   != null) req.setAttribute("msg",   msg);
        if (error != null) req.setAttribute("error", error);

        req.getRequestDispatcher("/jsp/TA/applicationStatus.jsp").forward(req, resp);
    }
}
