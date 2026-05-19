package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for processing MO applicant hiring operation
 * <p>Handles the request to hire a TA applicant by updating application status to HIRED.
 * Validates permissions and checks maximum hiring limits. Returns result message and forwards back to applicant list page.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-04-15
 */
@WebServlet("/hireApplicant")
public class HireApplicantServlet extends HttpServlet {

    /**
     * Handle POST request for hiring an applicant
     * @param request HTTP request containing moId, password, appId
     * @param response HTTP response with hiring result
     * @throws ServletException if servlet processing fails
     * @throws IOException if I/O error occurs during forwarding
     */
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
            request.setAttribute("msg", "Hire failed! Exceeded max hire limit.");
        }

        request.setAttribute("moId", moId);
        request.setAttribute("password", password);
        
        request.getRequestDispatcher("/jsp/MO_1/moApplicantList.jsp").forward(request, response);
    }
}
