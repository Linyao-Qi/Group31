package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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

        HttpSession session = request.getSession(false);
        if (session == null || !"MO".equals(session.getAttribute("userType")) || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String moId = (String) session.getAttribute("userId");
        String subject = request.getParameter("subject");
        String workType = request.getParameter("workType");
        String description = request.getParameter("description");
        String skillRequirement = request.getParameter("skillRequirement");
        String hoursPerWeekStr = request.getParameter("hoursPerWeek");
        String compensation = request.getParameter("compensation");
        String maxHireStr = request.getParameter("maxHire");

        int hoursPerWeek = 0;
        try {
            hoursPerWeek = Integer.parseInt(hoursPerWeekStr.trim());
        } catch (Exception e) {
            request.setAttribute("msg", "Publish failed: Hours must be a number!");
            request.getRequestDispatcher("/jsp/MO_1/publishJob.jsp").forward(request, response);
            return;
        }
        int maxHire = 1;
        try {
            maxHire = Integer.parseInt(maxHireStr.trim());
        } catch (Exception e) {
            request.setAttribute("msg", "Publish failed: Max hire must be a number!");
            request.getRequestDispatcher("/jsp/MO_1/publishJob.jsp").forward(request, response);
            return;
        }

        // 调用新的 publishJob 方法
        MoService moService = new MoService();
        Job job = moService.publishJobForMo(
                moId,
                subject,
                workType,
                description,
                skillRequirement,
                hoursPerWeek,
                compensation,
                maxHire
        );

        if (job != null) {
            request.setAttribute("msg", "Publish successful! Job ID: " + job.getJobId());
        } else {
            request.setAttribute("msg", "Publish failed: invalid params!");
        }

        request.getRequestDispatcher("/jsp/MO_1/publishJob.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
