package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/moApplicantReview")
public class MoApplicantReviewServlet extends HttpServlet {

    private MoApplicantReviewService service = new MoApplicantReviewService();

    // ✅ 新增init方法，统一初始化所有Service
    @Override
    public void init() throws ServletException {
        super.init();
        // 按顺序初始化，保证路径正确
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
        MoApplicantReviewService.init(getServletContext()); // 关键：调用MoApplicantReviewService的init
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

        String moId = request.getParameter("moId");
        String password = request.getParameter("password");

        if (!service.isValidMO(moId, password)) {
            request.setAttribute("msg", "Login failed: Please use a valid MO account!");
            request.getRequestDispatcher("/jsp/MO_1/applicantReview.jsp").forward(request, response);
            return;
        }

        List<Application> apps = service.getApplications(moId);
        List<Integer> scores = new ArrayList<>();

        for (Application app : apps) {
            String jobSkill = service.getSkillRequirement(app.getJobId());
            String taSkill = service.getTaSkill(app.getTaId());
            int score = SkillMatchUtil.calculateMatchScore(jobSkill, taSkill);
            scores.add(score);
        }

        request.setAttribute("apps", apps);
        request.setAttribute("scores", scores);
        request.getRequestDispatcher("/jsp/MO_1/applicantReview.jsp").forward(request, response);
    }
}