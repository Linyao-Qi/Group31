<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Application" %>
<%@ page import="com.MoService" %>
<%@ page import="com.CsvFileUtil" %>
<html>
<head>
    <title>All TA Applications</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 980px; margin: 50px auto; padding: 0 20px; background: #f8fafc; color: #1f2937;}
        h2 {font-size: 32px; margin: 34px 0 24px;}
        table {width: 100%; border-collapse: collapse; margin-top: 20px; background: #fff; border-radius: 8px; overflow: hidden; box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);}
        th, td {border-bottom: 1px solid #e5e7eb; padding: 12px; text-align: center;}
        th {background: #eff6ff; color: #1e3a8a; font-size: 14px;}
        tr:last-child td {border-bottom: none;}
        .nav {display: flex; justify-content: center; gap: 8px; flex-wrap: nowrap; width: min(980px, calc(100vw - 40px)); margin: 0 0 30px 50%; padding: 10px; background: #eff6ff; border: 1px solid #bfdbfe; border-radius: 8px; box-shadow: 0 6px 18px rgba(37, 99, 235, 0.10); transform: translateX(-50%);}
        .nav a {padding: 9px 14px; color: #1d4ed8; text-decoration: none; font-weight: bold; border-radius: 6px;}
        .nav a:hover {background: #dbeafe; text-decoration: none;}
        .empty {text-align: center; margin: 30px auto; padding: 18px; color: #64748b; background: #fff; border: 1px solid #e5e7eb; border-radius: 8px; box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);}
        .accepted, .pending, .rejected {display: inline-block; min-width: 78px; padding: 5px 10px; border-radius: 999px; font-weight: bold;}
        .accepted {background: #dcfce7; color: #166534;}
        .pending {background: #fef3c7; color: #b45309;}
        .rejected {background: #fee2e2; color: #991b1b;}
        .auth-box {
            width: min(560px, 100%);
            margin: 0 auto 30px;
            padding: 24px;
            background: #fff;
            border: 1px solid #dbe3ef;
            border-radius: 8px;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
        }
        .auth-box div {
            margin: 15px 0;
        }
        .auth-box label {
            display: block;
            margin-bottom: 7px;
            color: #111827;
            font-weight: bold;
        }
        .auth-box input {
            width: 100%;
            box-sizing: border-box;
            padding: 10px;
            border: 1px solid #cbd5e1;
            border-radius: 5px;
            font-size: 14px;
            background: #fff;
        }
        .auth-box input:focus {
            outline: none;
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }
        .auth-box button {
            width: 100%;
            padding: 12px;
            background: #2563eb;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }
        .auth-box button:hover {
            background: #1d4ed8;
        }
        .error {
            width: min(560px, 100%);
            margin: 0 auto;
            padding: 12px;
            background: #fee2e2;
            color: #991b1b;
            text-align: center;
            font-weight: bold;
            border-radius: 6px;
        }
    </style>
</head>
<body>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/hireApplicant.jsp">Hire Applicant</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">Application List</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/applicantReview.jsp">Skill Match Score</a>
    </div>

    <h2 align="center">TA Application List</h2>

    <%
        request.setCharacterEncoding("UTF-8");
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        MoService moService = new MoService();
        List<Application> appList = null;
        boolean needLogin = true;
        String errorMsg = "";

        if (userId != null && password != null) {
            appList = moService.getAllApps(userId, password, false);
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
                    <label>User ID (MO):</label>
                    <input type="text" name="userId" required>
                </div>
                <div>
                    <label>Password:</label>
                    <input type="password" name="password" required>
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
                    } else if ("REJECTED".equals(app.getAppStatus())) {
                        out.print("<td class='rejected'>" + app.getAppStatus() + "</td>");
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
