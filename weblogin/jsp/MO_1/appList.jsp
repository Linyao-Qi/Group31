<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Application" %>
<%@ page import="com.MoService" %>
<%@ page import="com.Job" %>
<html>
<head>
    <title>All TA Applications</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 980px; margin: 50px auto; padding: 0 20px; background: #f8fafc; color: #1f2937;}
        h2 {font-size: 24px; margin: 34px 0 24px; text-align: center;}
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background: #fff;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 16px; 
            text-align: center;
            font-size: 15px; 
        }
        th {
            background-color: #f8fafc;
            color: #000;
            font-weight: bold;
            font-size: 15px;
        }
        
        .nav {display: flex; justify-content: center; gap: 8px; flex-wrap: nowrap; width: min(980px, calc(100vw - 40px)); margin: 0 0 30px 50%; padding: 10px; background: #eff6ff; border-radius: 8px; transform: translateX(-50%);}
        .nav a {padding: 9px 14px; color: #1d4ed8; text-decoration: none; font-weight: bold; border-radius: 6px;}
        .nav a:hover {background: #dbeafe; text-decoration: none;}
        
        .empty {text-align: center; margin: 30px auto; padding: 18px; color: #64748b; background: #fff; border: 1px solid #e5e7eb; border-radius: 8px; box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);}
        
        .accepted {
            font-weight: bold;
            color: #16a34a;
            background: transparent !important;
        }
        .pending {
            font-weight: bold;
            color: #f59e0b;
            background: transparent !important;
        }
        .rejected {
            font-weight: bold;
            color: #dc2626;
            background: transparent !important;
        }

        .current-mo {margin: 0 auto 26px; padding: 14px; border-radius: 6px; background: #dbeafe; color: #1e3a8a; text-align: center; font-size: 20px;}
        .logout-form {margin: 28px auto 0; width: min(560px, 100%);}
        .logout-btn {width: 100%; padding: 12px; background: #94a3b8; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;}
        .logout-btn:hover {background: #7c8da3;}
    </style>
</head>
<body>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp">Hire Applicant</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">Application List</a>
        <a href="${pageContext.request.contextPath}/moApplicantReview">Skill Match Score</a>
    </div>

    <h2>TA Application List</h2>

    <%
        request.setCharacterEncoding("UTF-8");
        String userId = (String) session.getAttribute("userId");
        if (!"MO".equals(session.getAttribute("userType")) || userId == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        MoService moService = new MoService();
        MoService.init(getServletContext());
        List<Application> appList = moService.getAllAppsForMo(userId);
        List<Job> jobList = moService.getAllJobs();
    %>

    <div class="current-mo">Current MO: <%= userId %></div>

    <%
        if (appList == null || appList.isEmpty()) {
            out.print("<div class='empty'>No application records yet.</div>");
        } else {
    %>
        <table>
            <tr>
                <th>App ID</th>
                <th>Job ID</th>
                <th>Subject</th>
                <th>Work Type</th>
                <th>TA ID</th>
                <th>Status</th>
            </tr>
            <%
                for (Application app : appList) {
                    String subject = "";
                    String workType = "";
                    for (Job job : jobList) {
                        if (app.getJobId().equals(job.getJobId())) {
                            subject = job.getSubject();
                            workType = job.getWorkType();
                            break;
                        }
                    }
            %>
            <tr>
                <td><%= app.getAppId() %></td>
                <td><%= app.getJobId() %></td>
                <td><%= subject %></td>
                <td><%= workType %></td>
                <td><%= app.getTaId() %></td>
                <td class="
                    <%= "ACCEPTED".equals(app.getAppStatus()) ? "accepted" : "" %>
                    <%= "REJECTED".equals(app.getAppStatus()) ? "rejected" : "" %>
                    <%= "PENDING".equals(app.getAppStatus()) ? "pending" : "" %>
                ">
                    <%= app.getAppStatus() %>
                </td>
            </tr>
            <% } %>
        </table>
    <% } %>

    <form class="logout-form" method="post" action="${pageContext.request.contextPath}/mo/logout">
        <button type="submit" class="logout-btn">Logout</button>
    </form>
</body>
</html>