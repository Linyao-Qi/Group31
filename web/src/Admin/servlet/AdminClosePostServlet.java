package Admin.servlet;

import Admin.AdminPostDomainService;
import Admin.AdminRecruitment;
import Admin.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AdminClosePostServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();
    private final AdminPostDomainService postDomainService = new AdminPostDomainService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AdminWebAuthGuard.ensureAuthenticated(req, resp)) {
            return;
        }


        HttpSession session = req.getSession();
        List<AdminRecruitment> drafts = AdminWebSessionState.getPostDraft(session);
        boolean hasUnsavedChanges = AdminWebSessionState.hasUnsavedPostChanges(session);
        if ("1".equals(req.getParameter("refresh")) || drafts == null || !hasUnsavedChanges) {
            reloadFromRepository(session);
            drafts = AdminWebSessionState.getPostDraft(session);
            hasUnsavedChanges = AdminWebSessionState.hasUnsavedPostChanges(session);
        }

        req.setAttribute("posts", drafts);
        req.setAttribute("openPostsCount", adminService.countOpenPosts(drafts));
        req.setAttribute("hasUnsavedChanges", hasUnsavedChanges);
        Object message = session.getAttribute("admin.post.message");
        if (message != null) {
            req.setAttribute("message", String.valueOf(message));
            session.removeAttribute("admin.post.message");
        }
        req.getRequestDispatcher("/jsp/admin/posts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AdminWebAuthGuard.ensureAuthenticated(req, resp)) {
            return;
        }


        HttpSession session = req.getSession();
        List<AdminRecruitment> drafts = AdminWebSessionState.getPostDraft(session);
        if (drafts == null) {
            reloadFromRepository(session);
            drafts = AdminWebSessionState.getPostDraft(session);
        }

        String action = safeParam(req.getParameter("action"));
        String jobId = safeParam(req.getParameter("jobId"));
        String message = null;

        if ("close".equals(action)) {
            if (postDomainService.setOpenStatus(drafts, jobId, false)) {
                AdminWebSessionState.setPostDraft(session, drafts);
                AdminWebSessionState.setUnsavedPostChanges(session, true);
            } else {
                message = "Close action failed.";
            }
        } else if ("open".equals(action)) {
            if (postDomainService.setOpenStatus(drafts, jobId, true)) {
                AdminWebSessionState.setPostDraft(session, drafts);
                AdminWebSessionState.setUnsavedPostChanges(session, true);
            } else {
                message = "Open action failed.";
            }
        } else if ("save".equals(action)) {
            boolean ok = adminService.saveAllPosts(drafts);
            if (ok) {
                reloadFromRepository(session);
                message = "Saved successfully.";
            } else {
                message = "Save failed.";
            }
        } else if ("refresh".equals(action)) {
            reloadFromRepository(session);
        }

        if (message != null) {
            session.setAttribute("admin.post.message", message);
        }
        resp.sendRedirect(req.getContextPath() + "/admin/posts");
    }


    private void reloadFromRepository(HttpSession session) {
        List<AdminRecruitment> source = adminService.getAllPosts();
        List<AdminRecruitment> drafts = new ArrayList<>(source);
        AdminWebSessionState.setPostDraft(session, drafts);
        AdminWebSessionState.setUnsavedPostChanges(session, false);
    }

    private String safeParam(String value) {
        return value == null ? "" : value.trim();
    }
}
