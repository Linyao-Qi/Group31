package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet for displaying MO applicant review data.
 * <p>Loads applications for the logged-in MO, calculates skill match results,
 * and forwards review data to the applicant review JSP.</p>
 *
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/moApplicantReview")
public class MoApplicantReviewServlet extends HttpServlet {

    /** Service used to load applications and matching input data */
    private MoApplicantReviewService service = new MoApplicantReviewService();

    /**
     * Initialize authentication, MO service, and applicant review service.
     *
     * @throws ServletException if servlet initialization fails
     */
    @Override
    public void init() throws ServletException {
        super.init();
        
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
        MoApplicantReviewService.init(getServletContext()); 
    }

    /**
     * Forward GET requests to POST processing for applicant review.
     *
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException if servlet processing fails
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Handle applicant review requests for authenticated MO users.
     *
     * @param request HTTP request containing session information
     * @param response HTTP response forwarding to the review page
     * @throws ServletException if servlet processing fails
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        HttpSession session = request.getSession(false);
        if (session == null || !"MO".equals(session.getAttribute("userType")) || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        String moId = (String) session.getAttribute("userId");

        List<Application> apps = service.getApplications(moId);
        List<SkillMatchUtil.MatchResult> matchResults = new ArrayList<>();

        for (Application app : apps) {
            String jobSkill = service.getSkillRequirement(app.getJobId());
            String taSkill = service.getTaSkill(app);
            matchResults.add(SkillMatchUtil.calculateMatchResult(jobSkill, taSkill));
        }

        request.setAttribute("apps", apps);
        request.setAttribute("matchResults", matchResults);
        request.getRequestDispatcher("/jsp/MO_1/applicantReview.jsp").forward(request, response);
    }
}
