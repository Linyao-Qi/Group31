package com;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/login") // 修改Servlet访问路径为moLogin
public class MoLoginServlet extends HttpServlet { // 类名改为MoLoginServlet

    @Override
    public void init() {
        System.out.println("========== MoLoginServlet 初始化成功 ==========");
        AuthUtil.init(getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("========== 进入 doGet ==========");
        request.getRequestDispatcher("/jsp/login/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("========== 进入 doPost MO登录请求 ==========");

        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        // 强制指定用户类型为MO，无需前端传递
        String userType = "MO";

        // ========== 打印提交的数据 ==========
        System.out.println("角色：" + userType);
        System.out.println("账号：" + userId);
        System.out.println("密码：" + password);

        // 参数校验
        if (userId == null || userId.isBlank() || password == null || password.isBlank()) {
            System.out.println("========== 错误：未填写完整 ==========");
            request.setAttribute("msg", "All fields are required!");
            request.getRequestDispatcher("/jsp/login/login.jsp").forward(request, response);
            return;
        }

        // 仅验证MO类型用户
        boolean ok = AuthUtil.authenticateMO(userId, password);
        System.out.println("MO 验证结果：" + ok);

        if (ok) {
            System.out.println("========== MO登录成功！跳转到 MO 页面 ==========");
            request.getSession().setAttribute("userType", userType);
            request.getSession().setAttribute("userId", userId);
            response.sendRedirect(request.getContextPath() + "/jsp/MO_1/publishJob.jsp");
        } else {
            System.out.println("========== MO登录失败！ ==========");
            request.setAttribute("msg", "Invalid ID or Password!");
            request.getRequestDispatcher("/jsp/login/login.jsp").forward(request, response);
        }
    }
}