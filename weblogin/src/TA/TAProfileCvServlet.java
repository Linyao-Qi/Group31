package TA;

import Admin.DataFileLocator;
import com.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet("/ta/profile/cv")
public class TAProfileCvServlet extends HttpServlet {

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
        TAProfile profile = TAProfileService.getProfileByTaId(taId);
        if (profile == null || profile.getCvPath() == null || profile.getCvPath().trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String cvPath = profile.getCvPath().replace('\\', '/');
        if (cvPath.contains("..") || cvPath.startsWith("/") || cvPath.contains(":")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        File cvFile = DataFileLocator.resolveDataFile(cvPath.replace("/", File.separator), TAProfileCvServlet.class);
        if (!cvFile.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String fileName = cvFile.getName();
        String contentType = getServletContext().getMimeType(fileName);
        resp.setContentType(contentType != null ? contentType : "application/octet-stream");
        resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName.replace("\"", "") + "\"");
        resp.setContentLengthLong(cvFile.length());

        try (FileInputStream in = new FileInputStream(cvFile);
             OutputStream out = resp.getOutputStream()) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        }
    }
}
