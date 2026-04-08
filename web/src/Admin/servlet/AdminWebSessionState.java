package Admin.servlet;

import Admin.AdminRecruitment;
import Admin.AdminWorkload;
import jakarta.servlet.http.HttpSession;

import java.util.List;


public final class AdminWebSessionState {
    public static final String AUTHENTICATED_KEY = "admin.web.authenticated";
    public static final String AUTHENTICATED_USER_KEY = "admin.web.username";
    public static final String WORKLOADS_DRAFT_KEY = "admin.web.workloads.draft";
    public static final String WORKLOADS_UNSAVED_KEY = "admin.web.workloads.unsaved";
    public static final String POSTS_DRAFT_KEY = "admin.web.posts.draft";
    public static final String POSTS_UNSAVED_KEY = "admin.web.posts.unsaved";

    private AdminWebSessionState() {
    }

    public static boolean isAuthenticated(HttpSession session) {
        Object flag = session.getAttribute(AUTHENTICATED_KEY);
        return flag instanceof Boolean && (Boolean) flag;
    }

    public static void setAuthenticated(HttpSession session, String username) {
        session.setAttribute(AUTHENTICATED_KEY, true);
        session.setAttribute(AUTHENTICATED_USER_KEY, username);
    }

    @SuppressWarnings("unchecked")
    public static List<AdminWorkload> getWorkloadDraft(HttpSession session) {
        return (List<AdminWorkload>) session.getAttribute(WORKLOADS_DRAFT_KEY);
    }

    public static void setWorkloadDraft(HttpSession session, List<AdminWorkload> workloads) {
        session.setAttribute(WORKLOADS_DRAFT_KEY, workloads);
    }

    public static boolean hasUnsavedWorkloadChanges(HttpSession session) {
        Object flag = session.getAttribute(WORKLOADS_UNSAVED_KEY);
        return flag instanceof Boolean && (Boolean) flag;
    }

    public static void setUnsavedWorkloadChanges(HttpSession session, boolean unsaved) {
        session.setAttribute(WORKLOADS_UNSAVED_KEY, unsaved);
    }

    @SuppressWarnings("unchecked")
    public static List<AdminRecruitment> getPostDraft(HttpSession session) {
        return (List<AdminRecruitment>) session.getAttribute(POSTS_DRAFT_KEY);
    }

    public static void setPostDraft(HttpSession session, List<AdminRecruitment> posts) {
        session.setAttribute(POSTS_DRAFT_KEY, posts);
    }

    public static boolean hasUnsavedPostChanges(HttpSession session) {
        Object flag = session.getAttribute(POSTS_UNSAVED_KEY);
        return flag instanceof Boolean && (Boolean) flag;
    }

    public static void setUnsavedPostChanges(HttpSession session, boolean unsaved) {
        session.setAttribute(POSTS_UNSAVED_KEY, unsaved);
    }
}
