package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet for Publishing New TA Job Positions
 * <p>Handles job creation requests from MO users, validates input parameters,
 * manages user sessions, and invokes MoService to store new job records in CSV.
 * Access is restricted to authenticated MO users only.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-04-18
 */
@WebServlet(name = "PublishJobServlet", value = "/publishJob")
public class PublishJobServlet extends HttpServlet {

    /**
     * Initialize service and authentication utilities on startup
     */
    @Override
    public void init() {
        MoService.init(getServletContext());
        AuthUtil.init(getServletContext());
    }

    /**
     * Handle POST request for job publishing
     * Validates session, parses form parameters, performs type conversion,
     * and calls service to create a new job position
     * @param request HTTP request containing job form data
     * @param response HTTP response with success/failure message
     * @throws ServletException if servlet processing error occurs
     * @throws IOException if I/O error occurs during forwarding
     */
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

    /**
     * Forward GET requests to POST method for unified processing
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
}
