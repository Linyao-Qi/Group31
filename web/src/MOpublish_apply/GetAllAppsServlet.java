package com;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/getAllApps", loadOnStartup = 1)
public class GetAllAppsServlet extends HttpServlet {
    private MoService moService = new MoService();

    @Override
    public void init() {
        AuthUtil.init(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String moId = request.getParameter("userId");
        String password = request.getParameter("password");

        List<Application> appList = moService.getAllApps(moId, password, false);

        if (appList != null) {
            request.setAttribute("appList", appList);
            request.setAttribute("msg", "Login successful! Total applications: " + appList.size());
        } else {
            request.setAttribute("msg", "Login failed: Invalid ID or password!");
        }

        request.getRequestDispatcher("/jsp/MO_1/allApps.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}