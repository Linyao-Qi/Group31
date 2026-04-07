package com;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/getAllApps")
public class GetAllAppsServlet extends HttpServlet {
    private MoService moService = new MoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 鑾峰彇璁よ瘉鍙傛暟
        String userId = request.getParameter("userId"); // moId/adminId
        String password = request.getParameter("password");
        String isAdminStr = request.getParameter("isAdmin"); // "true"/"false"
        boolean isAdmin = "true".equals(isAdminStr);

        // 璋冪敤璁よ瘉鍚庣殑getAllApps鏂规硶
        List<Application> appList = moService.getAllApps(userId, password, isAdmin);

        if (appList != null) {
            request.setAttribute("appList", appList);
            request.setAttribute("msg", "璁よ瘉鎴愬姛锛佸叡鏌ヨ鍒� " + appList.size() + " 鏉＄敵璇疯褰�");
        } else {
            request.setAttribute("msg", "鏌ョ湅澶辫触锛佽韩浠借璇佸け璐ワ紙璐﹀彿/瀵嗙爜閿欒锛�");
        }
        request.getRequestDispatcher("/allApps.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}