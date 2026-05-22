package com;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/moApplicantReview")
public class MoApplicantReviewServlet extends HttpServlet {
    // 修正：实例化正确的Service类
    private MoApplicantReviewService service = new MoApplicantReviewService();

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

        // 获取MO账号密码
        String moId = request.getParameter("moId");
        String password = request.getParameter("password");

        // MO身份认证
        if (!service.isValidMO(moId, password)) {
            // 👇 只加这里：登录失败 → 传递错误提示
            request.setAttribute("msg", "Login failed: Please use a valid MO account (mo001 - mo005) to log in!");
            request.setAttribute("msgType", "error");
            
            // 转发回JSP显示提示
            request.getRequestDispatcher("/applicantReview.jsp").forward(request, response);
            return;
        }

        // 获取当前MO的申请列表（虚拟数据）
        List<Application> apps = service.getApplications(moId);
        List<Integer> scores = new ArrayList<>();

        // 逐个计算技能匹配分
        for (Application app : apps) {
            String jobReq = service.getJobReq(app.getJobId());
            String taSkill = service.getTaSkill(app.getTaId());
            int score = SkillMatchUtil.calculateMatchScore(jobReq, taSkill);
            scores.add(score);
        }

        // 👇 只加这里：登录成功 → 传递成功提示
        request.setAttribute("msg", "Login successful! Welcome, " + moId);
        request.setAttribute("msgType", "success");

        // 转发数据到JSP
        request.setAttribute("apps", apps);
        request.setAttribute("scores", scores);
        request.getRequestDispatcher("/applicantReview.jsp").forward(request, response);
    }
}