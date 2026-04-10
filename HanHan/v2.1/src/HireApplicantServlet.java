package com;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/hireApplicant")
public class HireApplicantServlet extends HttpServlet {
    private MoService moService = new MoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String moId = request.getParameter("moId");
        String appId = request.getParameter("appId");

        Application app = moService.acceptApplicant(moId, appId);

        if (app != null) {
            request.setAttribute("msg", "录用成功！申请ID：" + appId + "，状态已改为ACCEPTED");
        } else {
            request.setAttribute("msg", "录用失败！原因：参数为空/申请不存在/无操作权限/申请非待审核状态");
        }
        request.getRequestDispatcher("/hireApplicant.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}