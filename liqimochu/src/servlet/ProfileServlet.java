package servlet;

import model.Profile;
import service.ProfileService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import java.io.IOException;

@MultipartConfig
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 获取表单数据
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        String email = request.getParameter("email");
        String skills = request.getParameter("skills");
        String major = request.getParameter("major");

        // 获取上传文件
        Part filePart = request.getPart("cv");
        String fileName = filePart.getSubmittedFileName();

        // 设置上传路径（不会被Tomcat清掉）
        String uploadPath = System.getProperty("user.home") + "/Group31/liqimochu/data/profile_uploads";
        java.io.File uploadDir = new java.io.File(uploadPath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        String filePath = uploadPath + "/" + fileName;
        filePart.write(filePath);

        // 构建对象
        Profile profile = new Profile(name, id, email, skills, major, filePath);

        // 保存
        ProfileService.saveProfile(profile);

        response.sendRedirect(request.getContextPath() + "/profile");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Profile profile = ProfileService.loadProfile();

        request.setAttribute("profile", profile);

        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }
}