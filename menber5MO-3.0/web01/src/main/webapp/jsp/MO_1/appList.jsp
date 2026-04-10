<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Application" %>
<%@ page import="com.MoService" %>
<%@ page import="com.CsvFileUtil" %>
<html>
<head>
    <title>All TA Applications</title>
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
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/hireApplicant.jsp">Hire Applicant</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/jobList.jsp">Job List</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">Application List</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/applicantReview.jsp">Skill Match Score</a>
    </div>

    <h2 align="center">TA Application List</h2>

    <%
        request.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
        String isAdminStr = request.getParameter("isAdmin");
        boolean isAdmin = "true".equals(isAdminStr);

        MoService moService = new MoService();
        List<Application> appList = null;
        boolean needLogin = true;
        String errorMsg = "";

        if (userId != null && password != null) {
            appList = moService.getAllApps(userId, password, isAdmin);
            if (appList != null) {
                needLogin = false;
            } else {
                errorMsg = "Authentication failed! Invalid ID or password.";
            }
        }
    %>

    <% if (needLogin) { %>
        <div class="auth-box">
            <form action="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp" method="post">
                <div>
                    <label>User ID (MO/Admin):</label>
                    <input type="text" name="userId" required>
                </div>
                <div>
                    <label>Password:</label>
                    <input type="password" name="password" required>
                </div>
                <div>
                    <label>Role:</label>
                    <input type="radio" name="isAdmin" value="false" checked> MO
                    <input type="radio" name="isAdmin" value="true"> Admin
                </div>
                <div style="text-align:center; margin-top:15px;">
                    <button type="submit">Authenticate & View</button>
                </div>
            </form>
        </div>
        <% if (!errorMsg.isEmpty()) { %>
            <div class="error"><%= errorMsg %></div>
        <% } %>
    <% } else { %>
        <%
            if (appList == null || appList.size() == 0) {
                out.print("<div class='empty'>No application records yet.</div>");
            } else {
        %>
        <table>
            <tr>
                <th>App ID</th>
                <th>Job ID</th>
                <th>TA ID</th>
                <th>Status</th>
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