package Admin.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * 登录拦截工具。
 * 未登录访问受保护页面时，统一跳转到登录页。
 */
public final class AdminWebAuthGuard {
    private AdminWebAuthGuard() {
    }

    /**
     * 校验当前请求是否已登录。
     * @return 已登录返回 true；未登录时会重定向并返回 false
     */
    public static boolean ensureAuthenticated(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null && AdminWebSessionState.isAuthenticated(session)) {
            return true;
        }
        resp.sendRedirect(req.getContextPath() + "/admin/login");
        return false;
    }
}
