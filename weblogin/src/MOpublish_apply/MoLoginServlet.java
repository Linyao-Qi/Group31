package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * MO Login Servlet
 * <p>Handles login requests for Module Organizer (MO) users.
 * Validates credentials via AuthUtil, manages user session,
 * and routes to the MO dashboard upon successful authentication.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-04-15
 */
@WebServlet("/login")
public class MoLoginServlet extends HttpServlet {

    /**
     * Servlet initialization method
     * Initializes the authentication utility on startup
     */
    @Override
    public void init() {
        System.out.println("========== MoLoginServlet initialized successfully ==========");
        AuthUtil.init(getServletContext());
    }

    /**
     * Handle GET request for login page
     * Forwards to the MO login form page
     * @param request HTTP request
     * @param response HTTP response
     * @throws ServletException if servlet error occurs
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("========== Enter doGet ==========");
        request.getRequestDispatcher("/jsp/login/login.jsp").forward(request, response);
    }

    /**
     * Handle POST request for MO login submission
     * Validates user ID and password, creates session if authenticated
     * @param request HTTP request containing userId and password
     * @param response HTTP response redirecting to dashboard or login page
     * @throws ServletException if servlet processing fails
     * @throws IOException if I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("========== Enter doPost MO Login Request ==========");

        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String userType = "MO";

        System.out.println("Role: " + userType);
        System.out.println("User ID: " + userId);
        System.out.println("Password: " + password);

        if (userId == null || userId.isBlank() || password == null || password.isBlank()) {
            System.out.println("========== Error: Empty fields ==========");
            request.setAttribute("msg", "All fields are required!");
            request.getRequestDispatcher("/jsp/login/login.jsp").forward(request, response);
            return;
        }

        boolean ok = AuthUtil.authenticateMO(userId, password);
        System.out.println("MO Authentication Result: " + ok);

        if (ok) {
            System.out.println("========== MO Login Success! Redirect to MO page ==========");
            request.getSession().setAttribute("userType", userType);
            request.getSession().setAttribute("userId", userId);
            response.sendRedirect(request.getContextPath() + "/jsp/MO_1/publishJob.jsp");
        } else {
            System.out.println("========== MO Login Failed! ==========");
            request.setAttribute("msg", "Invalid ID or Password!");
            request.getRequestDispatcher("/jsp/login/login.jsp").forward(request, response);
        }
    }
}
