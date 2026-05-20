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

/**
 * Servlet for the standalone TA job browser page.
 * <p>
 * It reads filter parameters from the request, delegates job search to
 * {@link TaJobService}, and forwards the result to the Ta_Job JSP page.
 *
 * @author Linyao Qi
 * @version 3
 */
@WebServlet("/taJobList")
public class JobListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient TaJobService taJobService;

    /**
     * Initialises the job service with the deployed web application's job CSV file.
     *
     * @throws ServletException if servlet initialisation fails
     */
    @Override
    public void init() throws ServletException {
        String dataPath = getServletContext().getRealPath("/data/job.csv");
        taJobService = new TaJobService(dataPath);
    }

    /**
     * Handles job browsing, filtering, and keyword search requests.
     *
     * @param request HTTP request containing optional keyword, subject, workType,
     *                and status parameters
     * @param response HTTP response used to render the JSP page
     * @throws ServletException if request forwarding fails
     * @throws IOException if request or response processing fails
     */
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

    /**
     * Reuses the GET workflow for form submissions.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException if request forwarding fails
     * @throws IOException if request or response processing fails
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Converts null request parameters into empty strings and trims whitespace.
     *
     * @param value raw request parameter value
     * @return trimmed value, or an empty string when the input is null
     */
    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }
}
