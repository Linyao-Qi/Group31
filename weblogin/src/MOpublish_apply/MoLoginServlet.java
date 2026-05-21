package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
        if ("checkTaId".equalsIgnoreCase(request.getParameter("action"))) {
            handleCheckTaId(request, response);
            return;
        }
        request.setAttribute("nextTaId", getNextTaId());
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
        String userType = request.getParameter("userType");
        String action = request.getParameter("action");
        if (userType == null || userType.isBlank()) {
            forwardWithLoginState(request, response, "Please select a user type!", "", userId, action);
            return;
        }

        if ("register".equalsIgnoreCase(action)) {
            handleRegister(request, response, userType, userId, password);
            return;
        }

        System.out.println("Role: " + userType);
        System.out.println("User ID: " + userId);
        System.out.println("Password: " + password);

        if (userId == null || userId.isBlank() || password == null || password.isBlank()) {
            System.out.println("========== Error: Empty fields ==========");
            forwardWithLoginState(request, response, "All fields are required!", userType, userId, action);
            return;
        }

        if ("TA".equalsIgnoreCase(userType)) {
            boolean ok = AuthUtil.authenticateTA(userId, password);
            System.out.println("TA Authentication Result: " + ok);

            if (ok) {
                System.out.println("========== TA Login Success! Redirect to TA page ==========");
                request.getSession().setAttribute("userType", "TA");
                request.getSession().setAttribute("userId", userId);
                request.getSession().setAttribute("taId", userId);
                response.sendRedirect(request.getContextPath() + "/ta/home");
            } else {
                System.out.println("========== TA Login Failed! ==========");
                forwardWithLoginState(request, response, "Invalid ID or Password!", "TA", userId, "");
            }
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
            forwardWithLoginState(request, response, "Invalid ID or Password!", "MO", userId, "");
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response,
                                String userType, String userId, String password)
            throws ServletException, IOException {
        String confirmPassword = request.getParameter("confirmPassword");

        if (!"TA".equalsIgnoreCase(userType)) {
            forwardWithLoginState(request, response, "Please select TA before registering.", userType, userId, "register");
            return;
        }
        if (userId == null || userId.isBlank()
                || password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {
            forwardWithLoginState(request, response, "All fields are required!", userType, userId, "register");
            return;
        }
        if (!password.equals(confirmPassword)) {
            forwardWithLoginState(request, response, "Passwords do not match!", userType, userId, "register");
            return;
        }

        String authFilePath = getServletContext().getRealPath("data/auth.csv");
        List<AuthUtil.Auth> authList = CsvFileUtil.readAuthListFromCsv(authFilePath);
        if (userIdExists(authList, userId)) {
            forwardWithLoginState(request, response, "User ID already exists!", userType, userId, "register");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(authFilePath, true))) {
            writer.newLine();
            writer.write("TA," + userId.trim() + "," + password.trim());
        }
        AuthUtil.init(getServletContext());

        request.setAttribute("selectedUserType", "TA");
        request.setAttribute("userIdValue", safe(userId));
        request.setAttribute("nextTaId", getNextTaId());
        request.setAttribute("msg", "Register success! Please login.");
        request.getRequestDispatcher("/jsp/login/login.jsp").forward(request, response);
    }

    private void handleCheckTaId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = safe(request.getParameter("userId"));
        response.setContentType("text/plain;charset=UTF-8");
        if (userId.isEmpty()) {
            response.getWriter().write("empty");
            return;
        }

        String authFilePath = getServletContext().getRealPath("data/auth.csv");
        List<AuthUtil.Auth> authList = CsvFileUtil.readAuthListFromCsv(authFilePath);
        response.getWriter().write(userIdExists(authList, userId) ? "exists" : "available");
    }

    private void forwardWithLoginState(HttpServletRequest request, HttpServletResponse response,
                                       String message, String userType, String userId, String action)
            throws ServletException, IOException {
        request.setAttribute("msg", message);
        request.setAttribute("selectedUserType", safe(userType).toUpperCase());
        request.setAttribute("userIdValue", safe(userId));
        request.setAttribute("selectedAction", safe(action).toLowerCase());
        request.setAttribute("nextTaId", getNextTaId());
        if ("register".equalsIgnoreCase(action)) {
            request.setAttribute("registerUserIdValue", safe(userId));
        }
        request.getRequestDispatcher("/jsp/login/login.jsp").forward(request, response);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean userIdExists(List<AuthUtil.Auth> authList, String userId) {
        String target = safe(userId);
        for (AuthUtil.Auth auth : authList) {
            if (auth.getUserId() != null && auth.getUserId().equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private String getNextTaId() {
        String authFilePath = getServletContext().getRealPath("data/auth.csv");
        List<AuthUtil.Auth> authList = CsvFileUtil.readAuthListFromCsv(authFilePath);
        int maxNumber = 0;
        int width = 3;
        for (AuthUtil.Auth auth : authList) {
            if (auth.getUserType() == null || !"TA".equalsIgnoreCase(auth.getUserType())) {
                continue;
            }
            String userId = safe(auth.getUserId()).toUpperCase();
            if (!userId.matches("TA\\d+")) {
                continue;
            }
            String numberPart = userId.substring(2);
            int number = Integer.parseInt(numberPart);
            if (number > maxNumber) {
                maxNumber = number;
                width = Math.max(3, numberPart.length());
            }
        }
        return "TA" + String.format("%0" + width + "d", maxNumber + 1);
    }
}
