package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MO 申请审核 — 显示真实 TA 申请及技能匹配分
 * 使用 MoService.getAllApps() 读取 application.csv 中的真实数据，
 * 替代原来生成合成申请的 MoApplicantReviewService。
 */
@WebServlet("/moApplicantReview")
public class MoApplicantReviewServlet extends HttpServlet {

    private final MoService moService = new MoService();

    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
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

        String moId     = request.getParameter("moId");
        String password = request.getParameter("password");

        if (!AuthUtil.authenticateMO(moId, password)) {
            request.setAttribute("msg", "Login failed: Please use a valid MO account!");
            request.getRequestDispatcher("/jsp/MO_1/applicantReview.jsp").forward(request, response);
            return;
        }

        // 读取真实申请数据（来自 application.csv）
        List<Application> apps = moService.getAllApps(moId, password, false);
        if (apps == null) apps = new ArrayList<>();

        // 建立 jobId -> skillRequirement 映射，用于技能匹配计算
        Map<String, String> jobSkillMap = new HashMap<>();
        for (Job job : moService.getAllJobs()) {
            jobSkillMap.put(job.getJobId(), job.getSkillRequirement());
        }

        // 计算每条申请的技能匹配分（app.skills vs job.skillRequirement）
        List<Integer> scores = new ArrayList<>();
        for (Application app : apps) {
            String jobSkill = jobSkillMap.getOrDefault(app.getJobId(), "");
            int score = SkillMatchUtil.calculateMatchScore(jobSkill, app.getSkills());
            scores.add(score);
        }

        request.setAttribute("apps", apps);
        request.setAttribute("scores", scores);
        request.getRequestDispatcher("/jsp/MO_1/applicantReview.jsp").forward(request, response);
    }
}
