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

@WebServlet("/ta/status")
public class TAApplicationStatusServlet extends HttpServlet {

    private final MoService moService = new MoService();

    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
        TAApplicationService.init(getServletContext());
    }

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

        // build jobId -> Job map for display
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
