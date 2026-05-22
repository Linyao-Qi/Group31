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

@WebServlet("/ta/apply")
@MultipartConfig
public class TAApplyServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        AuthUtil.init(getServletContext());
        MoService.init(getServletContext());
        TAApplicationService.init(getServletContext());
        TAProfileService.init(getServletContext());
    }

    /** GET — 展示申请表单 */
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
                req.setAttribute("error", "You have already applied for this job. You can withdraw it in <a href=\"" + req.getContextPath() + "/ta/status\">My Applications</a>.");
            } else {
                req.setAttribute("error", "You have already applied for this job (status: " + existingStatus + ").");
            }
        }

        req.setAttribute("job", job);
        req.setAttribute("profile", TAProfileService.getProfileByTaId(taId));
        req.getRequestDispatcher("/jsp/TA/applyJob.jsp").forward(req, resp);
    }

    /** POST — 提交申请 */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("taId") == null) {
            resp.sendRedirect(req.getContextPath() + "/ta/login");
            return;
        }

        String taId  = (String) session.getAttribute("taId");
        String jobId = req.getParameter("jobId");
        String moId  = req.getParameter("moId");
        String name  = req.getParameter("name");
        String major = req.getParameter("major");
        String intro = req.getParameter("intro");
        String skills = req.getParameter("skills");
        String email = req.getParameter("email");

        // 处理 CV 上传，按 jobId 隔离目录避免不同职位的 CV 互相覆盖
        String cvPath = "";
        Part cvPart = req.getPart("cv");
        if (cvPart == null || cvPart.getSize() == 0) {
            Job job2 = TAApplicationService.getJobById(jobId);
            req.setAttribute("job", job2);
            req.setAttribute("profile", TAProfileService.getProfileByTaId(taId));
            req.setAttribute("error", "CV upload is required.");
            req.getRequestDispatcher("/jsp/TA/applyJob.jsp").forward(req, resp);
            return;
        }
        String fileName = cvPart.getSubmittedFileName();
        File uploadDir = DataFileLocator.resolveDataFile("cvs" + File.separator + taId + File.separator + jobId, TAApplyServlet.class);
        uploadDir.mkdirs();
        cvPart.write(new File(uploadDir, fileName).getAbsolutePath());
        cvPath = "cvs/" + taId + "/" + jobId + "/" + fileName;

        String result = TAApplicationService.applyForJob(
                taId, jobId, moId, name, major, intro, skills, email, cvPath);

        Job job = TAApplicationService.getJobById(jobId);
        TAProfile profile = TAProfileService.getProfileByTaId(taId);
        req.setAttribute("job", job);
        req.setAttribute("profile", profile);

        if ("SUCCESS".equals(result)) {
            req.setAttribute("msg", "Application submitted successfully!");
        } else if ("DUPLICATE".equals(result)) {
            String dupStatus = TAApplicationService.getActiveApplicationStatus(taId, jobId);
            if ("ACCEPTED".equals(dupStatus)) {
                req.setAttribute("error", "You have been accepted for this job. No further action needed.");
            } else {
                req.setAttribute("error", "You have already applied for this job. You can withdraw it in <a href=\"" + req.getContextPath() + "/ta/status\">My Applications</a>.");
            }
        } else {
            req.setAttribute("error", "Failed to submit application. Please try again.");
        }

        req.getRequestDispatcher("/jsp/TA/applyJob.jsp").forward(req, resp);
    }
}
