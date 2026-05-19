package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Servlet for retrieving all applications for MO users
 * <p>Handles login validation and application list query for Module Organizers.
 * Forwards authenticated MO users to the application management page with full application data.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet(value = "/getAllApps", loadOnStartup = 1)
public class GetAllAppsServlet extends HttpServlet {

    /**
     * Service instance for MO business logic
     */
    private MoService moService = new MoService();

    /**
     * Initialize authentication utility on servlet startup
     * @throws ServletException if initialization fails
     */
    @Override
    public void init() {
        AuthUtil.init(getServletContext());
    }

    /**
     * Handle POST requests for application list retrieval
     * Validates MO credentials and loads all related applications
     * @param request HTTP request containing userId and password
     * @param response HTTP response forwarding to application list page
     * @throws ServletException servlet processing error
     * @throws IOException I/O error during forwarding
     */
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

    /**
     * Forward GET requests to POST method
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException servlet processing error
     * @throws IOException I/O error during forwarding
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
