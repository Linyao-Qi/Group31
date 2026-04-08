package com;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(value = "/getAllApps", loadOnStartup = 1)
public class GetAllAppsServlet extends HttpServlet {
    private MoService moService = new MoService();

    @Override
    public void init() {
        AuthUtil.init(getServletContext());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取认证参数
        String userId = request.getParameter("userId"); // moId/adminId
        String password = request.getParameter("password");
        String isAdminStr = request.getParameter("isAdmin"); // "true"/"false"
        boolean isAdmin = "true".equals(isAdminStr);

        // 调用认证后的getAllApps方法
        List<Application> appList = moService.getAllApps(userId, password, isAdmin);

        if (appList != null) {
            request.setAttribute("appList", appList);
            request.setAttribute("msg", "认证成功！共查询到 " + appList.size() + " 条申请记录");
        } else {
            request.setAttribute("msg", "查看失败！身份认证失败（账号/密码错误）");
        }
        request.getRequestDispatcher("/jsp/MO_1/allApps.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}