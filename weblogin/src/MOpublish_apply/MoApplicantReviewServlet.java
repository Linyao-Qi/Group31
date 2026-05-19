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

@WebServlet("/moApplicantReview")
public class MoApplicantReviewServlet extends HttpServlet {

    private MoApplicantReviewService service = new MoApplicantReviewService();

    
    @Override
    public void init() throws ServletException {
        super.init();
        
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
        MoApplicantReviewService.init(getServletContext()); 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }

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
