package Admin.servlet;

import Admin.AdminService;
import Admin.AdminWorkload;
import Admin.AdminWorkloadDomainService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AdminWorkloadServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();
    private final AdminWorkloadDomainService workloadDomainService = new AdminWorkloadDomainService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AdminWebAuthGuard.ensureAuthenticated(req, resp)) {
            return;
        }

        HttpSession session = req.getSession();
        if ("1".equals(req.getParameter("refresh"))) {
            reloadFromRepository(session);
        }

        List<AdminWorkload> drafts = AdminWebSessionState.getWorkloadDraft(session);
        if (drafts == null) {
            reloadFromRepository(session);
            drafts = AdminWebSessionState.getWorkloadDraft(session);
        }

        String moduleCode = safeParam(req.getParameter("moduleCode"));
        String status = safeParam(req.getParameter("status"));
        String moId = safeParam(req.getParameter("moId"));

        List<AdminWorkload> filtered = workloadDomainService.filterWorkloads(drafts, moduleCode, status, moId);
        req.setAttribute("moduleCodes", workloadDomainService.collectModuleCodes(drafts));
        req.setAttribute("statuses", workloadDomainService.collectStatuses(drafts));
        req.setAttribute("moIds", workloadDomainService.collectMoIds(drafts));
        req.setAttribute("selectedModuleCode", moduleCode);
        req.setAttribute("selectedStatus", status);
        req.setAttribute("selectedMoId", moId);
        req.setAttribute("workloads", filtered);
        req.setAttribute("totalActiveTAs", adminService.getTotalActiveTAs(filtered));
        req.setAttribute("totalAssignedModules", adminService.getTotalAssignedModules(filtered));
        req.setAttribute("overloadedCount", adminService.getOverloadedCount(filtered));
        req.setAttribute("hasUnsavedChanges", AdminWebSessionState.hasUnsavedWorkloadChanges(session));

        Object message = session.getAttribute("admin.workload.message");
        if (message != null) {
            req.setAttribute("message", String.valueOf(message));
            session.removeAttribute("admin.workload.message");
        }
        req.getRequestDispatcher("/jsp/Admin/workloads.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!AdminWebAuthGuard.ensureAuthenticated(req, resp)) {
            return;
        }

        HttpSession session = req.getSession();
        List<AdminWorkload> drafts = AdminWebSessionState.getWorkloadDraft(session);
        if (drafts == null) {
            reloadFromRepository(session);
            drafts = AdminWebSessionState.getWorkloadDraft(session);
        }

        String action = safeParam(req.getParameter("action"));
        String message = null;
        if ("reassign".equals(action)) {
            String taId = safeParam(req.getParameter("taId"));
            String moduleCode = safeParam(req.getParameter("moduleCode"));
            String moId = safeParam(req.getParameter("moId"));
            if (workloadDomainService.markReassigning(drafts, taId, moduleCode, moId)) {
                adminService.recalculateTotalsAndStatusesInMemory(drafts);
                AdminWebSessionState.setWorkloadDraft(session, drafts);
                AdminWebSessionState.setUnsavedWorkloadChanges(session, true);
            } else {
                message = "Target workload not found or is not overloaded.";
            }
        } else if ("save".equals(action)) {
            boolean ok = adminService.saveAllWorkloads(drafts);
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
            session.setAttribute("admin.workload.message", message);
        }

        String moduleCode = safeParam(req.getParameter("selectedModuleCode"));
        String status = safeParam(req.getParameter("selectedStatus"));
        String moId = safeParam(req.getParameter("selectedMoId"));
        resp.sendRedirect(req.getContextPath() + "/admin/workloads?moduleCode=" + moduleCode + "&status=" + status + "&moId=" + moId);
    }


    private void reloadFromRepository(HttpSession session) {
        List<AdminWorkload> source = adminService.getAllWorkloads();
        List<AdminWorkload> drafts = new ArrayList<>(source);
        AdminWebSessionState.setWorkloadDraft(session, drafts);
        AdminWebSessionState.setUnsavedWorkloadChanges(session, false);
    }

    private String safeParam(String value) {
        return value == null ? "" : value.trim();
    }
}
