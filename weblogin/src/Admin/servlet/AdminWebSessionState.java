package Admin.servlet;

import Admin.AdminRecruitment;
import Admin.AdminWorkload;
import jakarta.servlet.http.HttpSession;

import java.util.List;


/**
 * Centralizes administrator session attribute names and typed accessors.
 *
 * @author Yutong Yao
 * @version 2.0
 */
public final class AdminWebSessionState {
    public static final String AUTHENTICATED_KEY = "admin.web.authenticated";
    public static final String AUTHENTICATED_USER_KEY = "admin.web.username";
    public static final String WORKLOADS_DRAFT_KEY = "admin.web.workloads.draft";
    public static final String WORKLOADS_UNSAVED_KEY = "admin.web.workloads.unsaved";
    public static final String POSTS_DRAFT_KEY = "admin.web.posts.draft";
    public static final String POSTS_UNSAVED_KEY = "admin.web.posts.unsaved";

    private AdminWebSessionState() {
    }

    /**
     * Checks whether the session belongs to an authenticated administrator.
     *
     * @param session HTTP session to inspect
     * @return true when the admin authentication flag is present and true
     */
    public static boolean isAuthenticated(HttpSession session) {
        Object flag = session.getAttribute(AUTHENTICATED_KEY);
        return flag instanceof Boolean && (Boolean) flag;
    }

    /**
     * Stores administrator authentication state in the session.
     *
     * @param session HTTP session to update
     * @param username authenticated administrator username
     */
    public static void setAuthenticated(HttpSession session, String username) {
        session.setAttribute(AUTHENTICATED_KEY, true);
        session.setAttribute(AUTHENTICATED_USER_KEY, username);
    }

    @SuppressWarnings("unchecked")
    /**
     * Returns the unsaved workload draft stored in the session.
     *
     * @param session HTTP session to inspect
     * @return workload draft list, or null when none exists
     */
    public static List<AdminWorkload> getWorkloadDraft(HttpSession session) {
        return (List<AdminWorkload>) session.getAttribute(WORKLOADS_DRAFT_KEY);
    }

    /**
     * Stores the current workload draft in the session.
     *
     * @param session HTTP session to update
     * @param workloads workload draft list
     */
    public static void setWorkloadDraft(HttpSession session, List<AdminWorkload> workloads) {
        session.setAttribute(WORKLOADS_DRAFT_KEY, workloads);
    }

    /**
     * Checks whether the workload draft has unsaved changes.
     *
     * @param session HTTP session to inspect
     * @return true when workload draft changes are pending
     */
    public static boolean hasUnsavedWorkloadChanges(HttpSession session) {
        Object flag = session.getAttribute(WORKLOADS_UNSAVED_KEY);
        return flag instanceof Boolean && (Boolean) flag;
    }

    /**
     * Sets the unsaved-change flag for workload drafts.
     *
     * @param session HTTP session to update
     * @param unsaved true when workload changes are pending
     */
    public static void setUnsavedWorkloadChanges(HttpSession session, boolean unsaved) {
        session.setAttribute(WORKLOADS_UNSAVED_KEY, unsaved);
    }

    @SuppressWarnings("unchecked")
    /**
     * Returns the unsaved recruitment-post draft stored in the session.
     *
     * @param session HTTP session to inspect
     * @return recruitment-post draft list, or null when none exists
     */
    public static List<AdminRecruitment> getPostDraft(HttpSession session) {
        return (List<AdminRecruitment>) session.getAttribute(POSTS_DRAFT_KEY);
    }

    /**
     * Stores the current recruitment-post draft in the session.
     *
     * @param session HTTP session to update
     * @param posts recruitment-post draft list
     */
    public static void setPostDraft(HttpSession session, List<AdminRecruitment> posts) {
        session.setAttribute(POSTS_DRAFT_KEY, posts);
    }

    /**
     * Checks whether the recruitment-post draft has unsaved changes.
     *
     * @param session HTTP session to inspect
     * @return true when recruitment-post changes are pending
     */
    public static boolean hasUnsavedPostChanges(HttpSession session) {
        Object flag = session.getAttribute(POSTS_UNSAVED_KEY);
        return flag instanceof Boolean && (Boolean) flag;
    }

    /**
     * Sets the unsaved-change flag for recruitment-post drafts.
     *
     * @param session HTTP session to update
     * @param unsaved true when post changes are pending
     */
    public static void setUnsavedPostChanges(HttpSession session, boolean unsaved) {
        session.setAttribute(POSTS_UNSAVED_KEY, unsaved);
    }
}

