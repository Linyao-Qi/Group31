package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "PublishJobServlet", value = "/publishJob")
public class PublishJobServlet extends HttpServlet {

    @Override
    public void init() {
        MoService.init(getServletContext());
        AuthUtil.init(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String moId = request.getParameter("moId");
        String password = request.getParameter("password");
        String subject = request.getParameter("subject");
        String workType = request.getParameter("workType");
        String description = request.getParameter("description");
        String skillRequirement = request.getParameter("skillRequirement");
        String hoursPerWeekStr = request.getParameter("hoursPerWeek");
        String compensation = request.getParameter("compensation");

        int hoursPerWeek = 0;
        try {
            hoursPerWeek = Integer.parseInt(hoursPerWeekStr.trim());
        } catch (Exception e) {
            request.setAttribute("msg", "Publish failed: Hours must be a number!");
            request.getRequestDispatcher("/jsp/MO_1/publishJob.jsp").forward(request, response);
            return;
        }

        // 调用新的 publishJob 方法
        MoService moService = new MoService();
        Job job = moService.publishJob(
                moId,
                password,
                subject,
                workType,
                description,
                skillRequirement,
                hoursPerWeek,
                compensation
        );

        if (job != null) {
            request.setAttribute("msg", "Publish successful! Job ID: " + job.getJobId());
        } else {
            request.setAttribute("msg", "Publish failed: Authentication failed or invalid params!");
        }

        request.getRequestDispatcher("/jsp/MO_1/publishJob.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}