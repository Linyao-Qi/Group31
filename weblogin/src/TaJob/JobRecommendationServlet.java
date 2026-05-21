package TaJob;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet for the TA smart job recommendation page.
 * <p>
 * The servlet always uses the logged-in TA ID stored in the session, so users
 * cannot request recommendations for another TA by editing query parameters.
 *
 * @author Linyao Qi
 * @version 3
 */
@WebServlet({"/ta/recommendations", "/taJobRecommendations"})
public class JobRecommendationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private transient JobRecommendationService recommendationService;

    /**
     * Initialises the recommendation service with deployed CSV file locations.
     *
     * @throws ServletException if servlet initialisation fails
     */
    @Override
    public void init() throws ServletException {
        String jobPath = getServletContext().getRealPath("/data/job.csv");
        String profilePath = getServletContext().getRealPath("/data/profiles.csv");
        recommendationService = new JobRecommendationService(jobPath, profilePath);
    }

    /**
     * Handles recommendation page requests for the current logged-in TA.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException if request forwarding fails
     * @throws IOException if request or response processing fails
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String taId = getLoggedInTaId(request);
        if (taId.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/ta/login");
            return;
        }
        TaProfileSnapshot profile = recommendationService.getProfile(taId);
        List<JobRecommendation> recommendations = recommendationService.recommendJobs(profile);

        request.setAttribute("taId", taId);
        request.setAttribute("profile", profile);
        request.setAttribute("recommendations", recommendations);
        request.getRequestDispatcher("/jsp/Ta_Job/recommendations.jsp").forward(request, response);
    }

    /**
     * Reads the current TA identifier from the HTTP session.
     *
     * @param request HTTP request containing the session
     * @return logged-in TA ID, or an empty string when no TA is logged in
     */
    private String getLoggedInTaId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("taId") != null) {
            return String.valueOf(session.getAttribute("taId")).trim();
        }
        return "";
    }
}
