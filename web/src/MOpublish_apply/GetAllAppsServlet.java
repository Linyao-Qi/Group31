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

        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String isAdminStr = request.getParameter("isAdmin");
        boolean isAdmin = "true".equals(isAdminStr);

        List<Application> appList = moService.getAllApps(userId, password, isAdmin);

        if (appList != null) {
            request.setAttribute("appList", appList);
            request.setAttribute("msg", "Authentication success. Total applications: " + appList.size());
        } else {
            request.setAttribute("msg", "Failed: Invalid ID or password!");
        }

        request.getRequestDispatcher("/jsp/MO_1/allApps.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}