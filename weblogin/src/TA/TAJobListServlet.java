package TA;

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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/ta/jobs")
public class TAJobListServlet extends HttpServlet {

    private final MoService moService = new MoService();

    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        String filterSubject  = req.getParameter("subject");
        String filterWorkType = req.getParameter("workType");
        String keyword        = req.getParameter("keyword");
        String focusJobId     = req.getParameter("focusJobId");

        List<Job> result = new ArrayList<>();
        for (Job job : moService.getAllJobs()) {
            if (!"OPEN".equals(job.getStatus())) continue;

            if (filterSubject != null && !filterSubject.isBlank()
                    && !job.getSubject().toLowerCase().contains(filterSubject.toLowerCase())) continue;

            if (filterWorkType != null && !filterWorkType.isBlank()
                    && !job.getWorkType().equalsIgnoreCase(filterWorkType)) continue;

            if (keyword != null && !keyword.isBlank()) {
                boolean match = job.getSubject().toLowerCase().contains(keyword.toLowerCase())
                        || job.getDescription().toLowerCase().contains(keyword.toLowerCase())
                        || job.getSkillRequirement().toLowerCase().contains(keyword.toLowerCase());
                if (!match) continue;
            }
            result.add(job);
        }

        req.setAttribute("jobs", result);
        req.setAttribute("filterSubject", filterSubject);
        req.setAttribute("filterWorkType", filterWorkType);
        req.setAttribute("keyword", keyword);
        req.setAttribute("focusJobId", focusJobId);
        req.getRequestDispatcher("/jsp/TA/jobList.jsp").forward(req, resp);
    }
}
