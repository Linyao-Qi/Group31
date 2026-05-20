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

/**
 * Servlet to handle TA CV (PDF) file viewing and downloading.
 * It securely serves the stored PDF file to the logged-in TA only.
 */
@WebServlet("/ta/profile/cv")
public class TAProfileCvServlet extends HttpServlet {

    /**
     * Initialize authentication and profile service on servlet startup
     */
    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        TAProfileService.init(getServletContext());
    }

    /**
     * Handle GET request: stream the CV PDF file to the browser
     * Security checks: login status, valid CV path, file existence, and path traversal protection
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Check if TA is logged in
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        // Get current logged-in TA's ID
        String taId = (String) session.getAttribute("taId");
        TAProfile profile = TAProfileService.getProfileByTaId(taId);

        // Return 404 if no profile or no CV uploaded
        if (profile == null || profile.getCvPath() == null || profile.getCvPath().trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Clean up and validate CV path to prevent path traversal attacks
        String cvPath = profile.getCvPath().replace('\\', '/');
        if (cvPath.contains("..") || cvPath.startsWith("/") || cvPath.contains(":")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // Resolve the actual CV file from the data directory
        File cvFile = DataFileLocator.resolveDataFile(cvPath.replace("/", File.separator), TAProfileCvServlet.class);
        if (!cvFile.isFile()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Set HTTP response headers for file streaming
        String fileName = cvFile.getName();
        String contentType = getServletContext().getMimeType(fileName);
        resp.setContentType(contentType != null ? contentType : "application/octet-stream");
        resp.setHeader("Content-Disposition", "inline; filename=\"" + fileName.replace("\"", "") + "\"");
        resp.setContentLengthLong(cvFile.length());

        // Stream the CV file to the client
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