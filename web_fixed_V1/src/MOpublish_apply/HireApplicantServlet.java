package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/hireApplicant")
public class HireApplicantServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String moId = request.getParameter("moId");
        String appId = request.getParameter("appId");

        MoService moService = new MoService();
        Application result = moService.acceptApplicant(moId, appId);

        if (result != null) {
            request.setAttribute("msg", "录用成功！申请者状态已更新");
        } else {
            request.setAttribute("msg", "录用失败：无权限/申请不存在/已处理");
        }

        request.getRequestDispatcher("/jsp/MO_1/moApplicantList.jsp").forward(request, response);
    }
}