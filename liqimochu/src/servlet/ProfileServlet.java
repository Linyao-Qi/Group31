package servlet;

import model.Profile;
import service.ProfileService;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class ProfileServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String id = request.getParameter("id");
        String email = request.getParameter("email");
        String skills = request.getParameter("skills");
        String major = request.getParameter("major");
        String cvPath = request.getParameter("cv");

        Profile profile = new Profile(name, id, email, skills, major, cvPath);
        ProfileService.saveProfile(profile);

        response.sendRedirect("profile.jsp");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Profile profile = ProfileService.loadProfile();
        request.setAttribute("profile", profile);
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}
