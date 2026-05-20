package TA;

import Admin.DataFileLocator;
import com.AuthUtil;
import com.Job;
import com.MoService;
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
 * TA Job Application Servlet
 * <p>Handles the complete job application workflow for teaching assistants.
 * GET requests display the application form for an open job, while POST
 * requests validate the application, process CV upload or profile CV reuse,
 * and persist the application record.</p>
 * @author Group31
 * @version 1.0
 * @since 2026-05-20
 */
@WebServlet("/ta/apply")
@MultipartConfig
public class TAApplyServlet extends HttpServlet {

    /**
     * Initializes authentication, job, application, and profile services.
     * @throws ServletException if servlet initialization fails
     */
    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
        TAApplicationService.init(getServletContext());
        TAProfileService.init(getServletContext());
    }

    /**
     * Displays the application form for an open job.
     * @param req HTTP request containing jobId and TA session
     * @param resp HTTP response used for redirecting or forwarding
     * @throws ServletException if request forwarding fails
     * @throws IOException if redirecting or forwarding fails
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        String jobId = req.getParameter("jobId");
        if (jobId == null || jobId.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/ta/jobs");
            return;
        }

        String taId = (String) session.getAttribute("taId");
        Job job = TAApplicationService.getJobById(jobId);
        if (job == null || !"OPEN".equals(job.getStatus())) {
            req.setAttribute("error", "This job is no longer available.");
            req.getRequestDispatcher("/jsp/TA/jobList.jsp").forward(req, resp);
            return;
        }

        String existingStatus = TAApplicationService.getActiveApplicationStatus(taId, jobId);
        if (existingStatus != null) {
            if ("ACCEPTED".equals(existingStatus)) {
                req.setAttribute("error", "You have been accepted for this job. No further action needed.");
            } else if ("PENDING".equals(existingStatus)) {
                req.setAttribute("error", "You have already applied for this job. You can withdraw it in <a href=\""
                        + req.getContextPath() + "/ta/status\">My Applications</a>.");
            } else {
                req.setAttribute("error", "You have already applied for this job (status: " + existingStatus + ").");
            }
        }

        req.setAttribute("job", job);
        req.setAttribute("profile", TAProfileService.getProfileByTaId(taId));
        req.getRequestDispatcher("/jsp/TA/applyJob.jsp").forward(req, resp);
    }

    /**
     * Submits a TA application and handles the required PDF CV.
     * @param req HTTP request containing application fields and optional CV upload
     * @param resp HTTP response used for redirecting or forwarding
     * @throws ServletException if multipart parsing or forwarding fails
     * @throws IOException if file upload, redirecting, or forwarding fails
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        String taId = (String) session.getAttribute("taId");
        String jobId = req.getParameter("jobId");
        String moId = req.getParameter("moId");
        String name = req.getParameter("name");
        String major = req.getParameter("major");
        String intro = req.getParameter("intro");
        String skills = req.getParameter("skills");
        String email = req.getParameter("email");
        boolean useProfileCv = !"false".equals(req.getParameter("useProfileCv"));
        TAProfile profile = TAProfileService.getProfileByTaId(taId);

        // Store uploaded CVs under a TA/job-specific folder to avoid overwriting files for other jobs.
        String cvPath = "";
        Part cvPart = req.getPart("cv");
        if (cvPart != null && cvPart.getSize() > 0) {
            String fileName = new File(cvPart.getSubmittedFileName()).getName();
            if (!isPdfFile(fileName)) {
                Job job2 = TAApplicationService.getJobById(jobId);
                req.setAttribute("job", job2);
                req.setAttribute("profile", profile);
                req.setAttribute("error", "Only PDF files can be uploaded as CV.");
                req.getRequestDispatcher("/jsp/TA/applyJob.jsp").forward(req, resp);
                return;
            }
            File uploadDir = DataFileLocator.resolveDataFile(
                    "cvs" + File.separator + taId + File.separator + jobId,
                    TAApplyServlet.class);
            uploadDir.mkdirs();
            cvPart.write(new File(uploadDir, fileName).getAbsolutePath());
            cvPath = "cvs/" + taId + "/" + jobId + "/" + fileName;
        } else if (useProfileCv && profile != null
                && profile.getCvPath() != null
                && !profile.getCvPath().trim().isEmpty()) {
            cvPath = profile.getCvPath();
        } else {
            Job job2 = TAApplicationService.getJobById(jobId);
            req.setAttribute("job", job2);
            req.setAttribute("profile", profile);
            req.setAttribute("error", "CV upload is required.");
            req.getRequestDispatcher("/jsp/TA/applyJob.jsp").forward(req, resp);
            return;
        }

        String result = TAApplicationService.applyForJob(
                taId, jobId, moId, name, major, intro, skills, email, cvPath);

        Job job = TAApplicationService.getJobById(jobId);
        req.setAttribute("job", job);
        req.setAttribute("profile", profile);

        if ("SUCCESS".equals(result)) {
            req.setAttribute("msg", "Application submitted successfully!");
        } else if ("DUPLICATE".equals(result)) {
            String dupStatus = TAApplicationService.getActiveApplicationStatus(taId, jobId);
            if ("ACCEPTED".equals(dupStatus)) {
                req.setAttribute("error", "You have been accepted for this job. No further action needed.");
            } else {
                req.setAttribute("error", "You have already applied for this job. You can withdraw it in <a href=\""
                        + req.getContextPath() + "/ta/status\">My Applications</a>.");
            }
        } else {
            req.setAttribute("error", "Failed to submit application. Please try again.");
        }

        req.getRequestDispatcher("/jsp/TA/applyJob.jsp").forward(req, resp);
    }

    /**
     * Checks whether the submitted CV file name uses a PDF extension.
     * @param fileName uploaded file name
     * @return true when the file name ends with .pdf
     */
    private boolean isPdfFile(String fileName) {
        return fileName != null && fileName.toLowerCase().endsWith(".pdf");
    }
}
