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
import java.util.List;

@WebServlet("/profile")
@MultipartConfig
public class ProfileServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");

        if (id != null) {
            List<Profile> list = ProfileService.getAllProfiles();
            for (Profile p : list) {
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

        // 获取上传文件
        Part filePart = request.getPart("cv");

        String fileName = null;

        if (filePart != null && filePart.getSize() > 0) {

            fileName = new File(filePart.getSubmittedFileName()).getName();

            // 保存路径（uploads文件夹）
            String uploadPath = System.getProperty("user.home") + "/Group31/liqimochu/web/profile_uploads";
            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) uploadDir.mkdir();

            filePart.write(uploadPath + File.separator + fileName);
        } else {
            // 编辑时没有重新上传 → 保留旧文件
            fileName = request.getParameter("existingCv");
        }

        Profile profile = new Profile(
                name, id, email, skills, major, fileName
        );

        ProfileService.saveOrUpdate(profile);

        response.sendRedirect("list");
    }
}
