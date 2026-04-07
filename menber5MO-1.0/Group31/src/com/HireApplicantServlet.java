package com;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            request.setAttribute("msg", "success" + appId + "state changed to ACCEPTED");
        } else {
            request.setAttribute("msg", "failed");
        }
        request.getRequestDispatcher("/hireApplicant.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}