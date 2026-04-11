<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.Application, java.util.List" %>
<html>
<head>
    <title>All Applications</title>
    <style>
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
        .success { color: green; font-weight: bold; text-align: center; }
        .error { color: red; font-weight: bold; text-align: center; }
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

    <h2>All Applications</h2>

    <%
        String msg = (String) request.getAttribute("msg");
        String msgType = (String) request.getAttribute("msgType");
    %>

    <% if (msg != null) { %>
        <div class="<%= msgType %>"><%= msg %></div>
    <% } %>

    <div class="login-form">
        <form action="${pageContext.request.contextPath}/moApplicantReview" method="post">
            <input type="text" name="moId" placeholder="MO ID" required>
            <input type="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>
    </div>

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