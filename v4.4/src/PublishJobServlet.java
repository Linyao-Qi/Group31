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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String moId = request.getParameter("moId");
        String password = request.getParameter("password");
        String jobName = request.getParameter("jobName");
        String jobRequirements = request.getParameter("jobRequirements");

        MoService moService = new MoService();
        Job job = moService.publishJob(moId, password, jobName, jobRequirements);

        if (job != null) {
            request.setAttribute("msg", "发布成功！");
        } else {
            request.setAttribute("msg", "发布失败：MO认证失败");
        }

        request.getRequestDispatcher("/publishJob.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}