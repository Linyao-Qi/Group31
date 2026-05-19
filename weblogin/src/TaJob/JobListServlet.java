package TaJob;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/taJobList")
public class JobListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient TaJobService taJobService;

    @Override
    public void init() throws ServletException {
        String dataPath = getServletContext().getRealPath("/data/job.csv");
        taJobService = new TaJobService(dataPath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String keyword = safeTrim(request.getParameter("keyword"));
        String subject = safeTrim(request.getParameter("subject"));
        String workType = safeTrim(request.getParameter("workType"));
        String status = safeTrim(request.getParameter("status"));

        List<JobPosting> jobs = taJobService.searchJobs(subject, workType, status, keyword);
        Set<String> subjects = taJobService.getAllSubjects();
        Set<String> workTypes = taJobService.getAllWorkTypes();
        Set<String> statuses = taJobService.getAllStatuses();

        request.setAttribute("jobs", jobs);
        request.setAttribute("subjects", subjects == null ? Collections.emptySet() : subjects);
        request.setAttribute("workTypes", workTypes == null ? Collections.emptySet() : workTypes);
        request.setAttribute("statuses", statuses == null ? Collections.emptySet() : statuses);

        request.setAttribute("keyword", keyword);
        request.setAttribute("subject", subject);
        request.setAttribute("workType", workType);
        request.setAttribute("status", status);

        request.getRequestDispatcher("/jsp/Ta_Job/jobList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
