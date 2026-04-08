<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Application" %>
<%@ page import="com.MoService" %>
<%@ page import="com.CsvFileUtil" %>
<html>
<head>
    <title>所有TA申请列表</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 0 20px;}
        table {width: 100%; border-collapse: collapse; margin-top: 20px;}
        th, td {border: 1px solid #ddd; padding: 12px; text-align: center;}
        th {background: #f8fafc; color: #333;}
        .nav {margin-bottom: 30px; text-align: center;}
        .nav a {margin: 0 10px; color: #2563eb; text-decoration: none;}
        .nav a:hover {text-decoration: underline;}
        .empty {text-align: center; margin-top: 30px; color: #666;}
        .accepted {color: #166534; font-weight: bold;}
        .pending {color: #f59e0b; font-weight: bold;}
        .auth-box {
            border: 1px solid #ddd;
            padding: 20px;
            width: 400px;
            margin: 0 auto 30px;
            border-radius: 8px;
        }
        .auth-box div {
            margin: 10px 0;
        }
        .auth-box label {
            display: inline-block;
            width: 120px;
            text-align: right;
            margin-right: 10px;
        }
        .error {
            color: red;
            text-align: center;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">发布岗位</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/hireApplicant.jsp">录用申请者</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/jobList.jsp">查看所有岗位</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">查看所有申请</a>
        <a href="applicantReview.jsp">查看申请及匹配分</a>
    </div>

    <h2 align="center">TA申请列表</h2>

    <%
        // 获取表单提交的认证参数
        request.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String isAdminStr = request.getParameter("isAdmin");
        boolean isAdmin = "true".equals(isAdminStr);

        MoService moService = new MoService();
        List<Application> appList = null;
        boolean needLogin = true;
        String errorMsg = "";

        // 如果提交了账号密码，就进行认证
        if (userId != null && password != null) {
            appList = moService.getAllApps(userId, password, isAdmin);
            if (appList != null) {
                needLogin = false; // 认证成功，不需要登录
            } else {
                errorMsg = "身份认证失败！账号或密码错误";
            }
        }
    %>

    <% if (needLogin) { %>
        <!-- 认证失败 / 未登录 → 显示登录表单 -->
        <div class="auth-box">
            <form action="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp" method="post">
                <div>
                    <label>用户ID（MO/Admin）：</label>
                    <input type="text" name="userId" required>
                </div>
                <div>
                    <label>密码：</label>
                    <input type="password" name="password" required>
                </div>
                <div>
                    <label>身份：</label>
                    <input type="radio" name="isAdmin" value="false" checked> MO
                    <input type="radio" name="isAdmin" value="true"> Admin
                </div>
                <div style="text-align:center; margin-top:15px;">
                    <button type="submit">验证身份并查看</button>
                </div>
            </form>
        </div>
        <% if (!errorMsg.isEmpty()) { %>
            <div class="error"><%= errorMsg %></div>
        <% } %>
    <% } else { %>
        <!-- 认证成功 → 显示申请列表 -->
        <%
            if (appList == null || appList.size() == 0) {
                out.print("<div class='empty'>暂无申请数据，请先添加申请</div>");
            } else {
        %>
        <table>
            <tr>
                <th>申请ID</th>
                <th>关联岗位ID</th>
                <th>申请者TA ID</th>
                <th>申请状态</th>
            </tr>
            <%
                for (Application app : appList) {
                    out.print("<tr>");
                    out.print("<td>" + app.getAppId() + "</td>");
                    out.print("<td>" + app.getJobId() + "</td>");
                    out.print("<td>" + app.getTaId() + "</td>");
                    if ("ACCEPTED".equals(app.getAppStatus())) {
                        out.print("<td class='accepted'>" + app.getAppStatus() + "</td>");
                    } else {
                        out.print("<td class='pending'>" + app.getAppStatus() + "</td>");
                    }
                    out.print("</tr>");
                }
            %>
        </table>
        <%
            }
        %>
    <% } %>
</body>
</html>
