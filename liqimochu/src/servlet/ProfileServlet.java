package servlet;

import model.Profile;
import service.ProfileService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@WebServlet("/profile")
@MultipartConfig
public class ProfileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null) {
            for (Profile p : ProfileService.getAllProfiles()) {
                if (p.getId().equals(id)) {
                    request.setAttribute("profile", p);
                    break;
                }
            }
        }
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String name = request.getParameter("name");
        String id = request.getParameter("id");
        String email = request.getParameter("email");
        String skills = request.getParameter("skills");
        String major = request.getParameter("major");

        Part filePart = request.getPart("cv");
        String fileName = null;

        String targetDir = System.getProperty("user.home") + "/Group31/liqimochu/web/profile_uploads";
        File dir = new File(targetDir);
        if (!dir.exists()) dir.mkdirs();

        if (filePart != null && filePart.getSize() > 0) {
            fileName = new File(filePart.getSubmittedFileName()).getName();

            File targetFile = new File(dir, fileName);

            try (InputStream in = filePart.getInputStream()) {
                Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } else {
            fileName = request.getParameter("existingCv");
            if (fileName == null) fileName = "";
        }

        Profile profile = new Profile(name, id, email, skills, major, fileName);
        ProfileService.saveOrUpdate(profile);

        response.sendRedirect("list");
    }
}