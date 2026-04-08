<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.Application, java.util.List" %>
<html>
<head>
    <title>All Applications</title>
    <style>
        /* 完全和 appList.jsp 样式统一 */
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 0 20px;
        }
        .nav {
            margin-bottom: 30px;
            text-align: center;
        }
        .nav a {
            margin: 0 10px;
            color: #2563eb;
            text-decoration: none;
        }
        .nav a:hover {
            text-decoration: underline;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: center;
        }
        th {
            background-color: #f8f9fa;
        }
        .login-form {
            text-align: center;
            margin-bottom: 30px;
        }
        .login-form input {
            padding: 8px;
            margin: 5px;
        }
        .login-form button {
            padding: 8px 16px;
            background-color: #2563eb;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        /* 只新增这两个提示样式 */
        .success { color: green; font-weight: bold; text-align: center; }
        .error { color: red; font-weight: bold; text-align: center; }
    </style>
</head>
<body>

    <div class="nav">
        <a href="publishJob.jsp">发布岗位</a>
        <a href="hireApplicant.jsp">录用申请者</a>
        <a href="jobList.jsp">查看所有岗位</a>
        <a href="appList.jsp">查看所有申请</a>
        <a href="applicantReview.jsp">查看申请及匹配分</a>
    </div>

    <h2>All Applications</h2>

    <%
        // 只新增：获取登录提示（不影响原有任何代码）
        String msg = (String) request.getAttribute("msg");
        String msgType = (String) request.getAttribute("msgType");
    %>

    <!-- 只新增：提示信息区域 -->
    <% if (msg != null) { %>
        <div class="<%= msgType %>"><%= msg %></div>
    <% } %>

    <!-- ======================= -->
    <!-- 你原来的登录表单（完全没动） -->
    <!-- ======================= -->
    <div class="login-form">
        <form action="moApplicantReview" method="post">
            <input type="text" name="moId" placeholder="MO ID" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>
    </div>

    <!-- 申请列表（你原来的代码，完全没动） -->
    <table>
        <tr>
            <th>Application ID</th>
            <th>Job ID</th>
            <th>TA ID</th>
            <th>Status</th>
            <th>Match Score</th>
        </tr>
        <%
        List<Application> apps = (List<Application>) request.getAttribute("apps");
        List<Integer> scores = (List<Integer>) request.getAttribute("scores");
        
        if (apps != null && !apps.isEmpty()) {
            for (int i = 0; i < apps.size(); i++) {
                Application app = apps.get(i);
                int score = scores.get(i);
        %>
        <tr>
            <td><%= app.getAppId() %></td>
            <td><%= app.getJobId() %></td>
            <td><%= app.getTaId() %></td>
            <td><%= app.getAppStatus() %></td>
            <td><%= score %> pts</td>
        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="5">No applications yet.</td>
        </tr>
        <%
        }
        %>
    </table>

</body>
</html>
