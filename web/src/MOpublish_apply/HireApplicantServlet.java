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
        String password = request.getParameter("password");
        String appId = request.getParameter("appId");

        MoService moService = new MoService();
        Application result = moService.acceptApplicant(moId, appId);

        if (result != null) {
            request.setAttribute("msg", "Hired successfully! Status updated.");
        } else {
            // 这里改成 超过最大录用人数 的提示！
            request.setAttribute("msg", "Hire failed! Exceeded max hire limit.");
        }

        // 把密码传回去，保持登录状态
        request.setAttribute("moId", moId);
        request.setAttribute("password", password);
        
        request.getRequestDispatcher("/jsp/MO_1/moApplicantList.jsp").forward(request, response);
    }
}