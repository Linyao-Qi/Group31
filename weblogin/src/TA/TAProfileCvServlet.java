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
 * TA Profile CV Servlet
 * <p>Streams the stored CV file for the logged-in teaching assistant. It
 * validates login status, checks the saved CV path, prevents path traversal,
 * and serves the file inline through the HTTP response.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/profile/cv")
public class TAProfileCvServlet extends HttpServlet {

    /**
     * Initializes authentication and profile services on servlet startup.
     * @throws ServletException if initialization fails
     */
    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        TAProfileService.init(getServletContext());
    }

    /**
     * Streams the current TA's CV file to the browser after security checks.
     * @param req HTTP request containing the TA session
     * @param resp HTTP response used to stream the CV or send errors
     * @throws ServletException if servlet processing fails
     * @throws IOException if file reading or response writing fails
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
