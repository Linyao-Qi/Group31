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

/**
 * TA Profile Servlet
 * <p>Handles teaching assistant profile operations, including viewing profile
 * details, updating personal information, uploading a PDF CV, and removing the
 * stored CV path.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/profile")
@MultipartConfig  // Required to handle file upload requests
public class TAProfileServlet extends HttpServlet {

    /**
     * Initializes authentication and profile services during servlet startup.
     * @throws ServletException if initialization fails
     */
    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        TAProfileService.init(getServletContext());
    }

    /**
     * Loads and displays the logged-in TA's profile page.
     * @param req HTTP request containing the TA session
     * @param resp HTTP response used for redirecting or forwarding
     * @throws ServletException if request forwarding fails
     * @throws IOException if redirecting or forwarding fails
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Check if the TA is logged in
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        // Get current logged-in TA's ID
        String taId = (String) session.getAttribute("taId");
        // Retrieve profile data and pass to the frontend JSP
        req.setAttribute("profile", TAProfileService.getProfileByTaId(taId));
        // Forward to profile page
        req.getRequestDispatcher("/jsp/TA/profile.jsp").forward(req, resp);
    }

    /**
     * Updates profile information and handles CV upload or removal.
     * @param req HTTP request containing profile form fields and optional CV upload
     * @param resp HTTP response used for redirecting or forwarding
     * @throws ServletException if multipart parsing or forwarding fails
     * @throws IOException if file upload, redirecting, or forwarding fails
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        // Validate login status
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        // Retrieve form parameters
        String taId    = (String) session.getAttribute("taId");
        String name    = req.getParameter("name");
        String email   = req.getParameter("email");
        String skills  = req.getParameter("skills");
        String major   = req.getParameter("major");

        // Keep existing CV path if available
        TAProfile existing = TAProfileService.getProfileByTaId(taId);
        String cvPath = (existing != null && existing.getCvPath() != null) ? existing.getCvPath() : "";

        String uploadedFileName = null;
        boolean removeCv = "true".equals(req.getParameter("removeCv"));

        // If user requests to remove CV
        if (removeCv) {
            cvPath = "";
        } else {
            // Get uploaded CV file part
            Part cvPart = req.getPart("cv");
            if (cvPart != null && cvPart.getSize() > 0) {
                uploadedFileName = new File(cvPart.getSubmittedFileName()).getName();

                // Validate file type: only PDF allowed
                if (!isPdfFile(uploadedFileName)) {
                    req.setAttribute("error", "Only PDF files can be uploaded as CV.");
                    req.setAttribute("profile", new TAProfile(taId, name, email, skills, major, cvPath));
                    req.getRequestDispatcher("/jsp/TA/profile.jsp").forward(req, resp);
                    return;
                }

                // Create upload directory and save the file
                File uploadDir = DataFileLocator.resolveDataFile("cvs" + File.separator + taId, TAProfileServlet.class);
                uploadDir.mkdirs();
                cvPart.write(new File(uploadDir, uploadedFileName).getAbsolutePath());

                // Update CV path stored in profile
                cvPath = "cvs/" + taId + "/" + uploadedFileName;
            }
        }

        // Create updated profile object and save to CSV
        TAProfile profile = new TAProfile(taId, name, email, skills, major, cvPath);
        TAProfileService.saveOrUpdateProfile(profile);

        // Set success message based on operation
        if (removeCv) {
            req.setAttribute("msg", "CV removed.");
        } else if (uploadedFileName != null) {
            req.setAttribute("msg", "Profile updated. CV uploaded: " + uploadedFileName);
        } else {
            req.setAttribute("msg", "Profile updated successfully.");
        }

        // Refresh and display updated profile
        req.setAttribute("profile", profile);
        req.getRequestDispatcher("/jsp/TA/profile.jsp").forward(req, resp);
    }

    /**
     * Checks whether the uploaded file name uses a PDF extension.
     * @param fileName name of the uploaded file
     * @return true if it ends with .pdf, false otherwise
     */
    private boolean isPdfFile(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }
}
