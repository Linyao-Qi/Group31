package TA;

import Admin.DataFileLocator;
import com.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

@WebServlet("/ta/profile")
@MultipartConfig
public class TAProfileServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        TAProfileService.init(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }
        String taId = (String) session.getAttribute("taId");
        req.setAttribute("profile", TAProfileService.getProfileByTaId(taId));
        req.getRequestDispatcher("/jsp/TA/profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        String taId    = (String) session.getAttribute("taId");
        String name    = req.getParameter("name");
        String email   = req.getParameter("email");
        String skills  = req.getParameter("skills");
        String major   = req.getParameter("major");

        TAProfile existing = TAProfileService.getProfileByTaId(taId);
        String cvPath = (existing != null && existing.getCvPath() != null) ? existing.getCvPath() : "";

        String uploadedFileName = null;
        boolean removeCv = "true".equals(req.getParameter("removeCv"));

        if (removeCv) {
            cvPath = "";
        } else {
            Part cvPart = req.getPart("cv");
            if (cvPart != null && cvPart.getSize() > 0) {
                uploadedFileName = cvPart.getSubmittedFileName();
                File uploadDir = DataFileLocator.resolveDataFile("cvs" + File.separator + taId, TAProfileServlet.class);
                uploadDir.mkdirs();
                cvPart.write(new File(uploadDir, uploadedFileName).getAbsolutePath());
                cvPath = "cvs/" + taId + "/" + uploadedFileName;
            }
        }

        TAProfile profile = new TAProfile(taId, name, email, skills, major, cvPath);
        TAProfileService.saveOrUpdateProfile(profile);

        if (removeCv) {
            req.setAttribute("msg", "CV removed.");
        } else if (uploadedFileName != null) {
            req.setAttribute("msg", "Profile updated. CV uploaded: " + uploadedFileName);
        } else {
            req.setAttribute("msg", "Profile updated successfully.");
        }
        req.setAttribute("profile", profile);
        req.getRequestDispatcher("/jsp/TA/profile.jsp").forward(req, resp);
    }
}
