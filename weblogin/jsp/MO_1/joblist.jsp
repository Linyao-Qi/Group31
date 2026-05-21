<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Job" %>
<%@ page import="com.MoService" %>
<%@ page import="com.AuthUtil" %>
<html>
<head>
    <title>TA Job List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 980px;
            margin: 50px auto;
            padding: 0 20px;
            background: #f8fafc;
            color: #1f2937;
        }

        .nav {display: flex; justify-content: center; gap: 8px; flex-wrap: nowrap; width: min(980px, calc(100vw - 40px)); margin: 0 0 30px 50%; padding: 10px; background: #eff6ff; border-radius: 8px; transform: translateX(-50%);}
        .nav a {padding: 9px 14px; color: #1d4ed8; text-decoration: none; font-weight: bold; border-radius: 6px;}
        .nav a:hover {background: #dbeafe; text-decoration: none;}

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background: #fff;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: center;
        }
        th {
            background-color: #f8fafc;
            color: #000;
            font-size: 14px;
            font-weight: bold;
        }
        h2 {
            text-align: center;
            font-size: 24px;
            margin-bottom: 24px;
        }
        .empty {
            text-align: center;
            margin-top: 30px;
            color: #666;
            font-size: 16px;
        }
        
        /* 完全统一：宽度、字体、样式 */
        .current-mo {
            width: 100%;
            box-sizing: border-box;
            margin: 0 auto 26px;
            padding: 10px;
            border-radius: 5px;
            background: #dbeafe;
            color: #1e3a8a;
            text-align: center;
            font-size: 14px;
            font-weight: normal;
        }

        .logout-form {margin: 28px auto 0; width: min(560px, 100%);}
        .logout-btn {width: 100%; padding: 12px; background: #94a3b8; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;}
        .logout-btn:hover {background: #7c8da3;}

        .status-open {
            color: #16a34a;
            font-weight: bold;
        }
        .status-filled {
            color: #dc2626;
            font-weight: bold;
        }
    </style>
</head>
<body>

<%
    MoService.init(getServletContext());
    AuthUtil.init(getServletContext());

    MoService moService = new MoService();
    List<Job> jobList = moService.getAllJobs();
    String currentMoId = (String) session.getAttribute("userId");
%>

    <div class="nav">
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp">Hire Applicant</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/joblist.jsp">Job List</a>
        <a href="${pageContext.request.contextPath}/moApplicantReview">Skill Match Score</a>
    </div>

    <h2>TA Job List</h2>
    <div class="current-mo">Current MO: <%= currentMoId %></div>

    <%
        if (jobList == null || jobList.isEmpty()) {
            out.print("<div class='empty'>No jobs available yet. Please publish a job first.</div>");
        } else {
    %>
    <table>
        <tr>
            <th>Job ID</th>
            <th>MO ID</th>
            <th>Subject</th>
            <th>Work Type</th>
            <th>Description</th>
            <th>Skill Requirement</th>
            <th>Hours/Week</th>
            <th>Compensation</th>
            <th>Max Hire</th>
            <th>Status</th>
        </tr>
        <%
            for (Job job : jobList) {
                out.print("<tr>");
                out.print("<td>" + job.getJobId() + "</td>");
                out.print("<td>" + job.getMoId() + "</td>");
                out.print("<td>" + job.getSubject() + "</td>");
                out.print("<td>" + job.getWorkType() + "</td>");
                out.print("<td>" + job.getDescription() + "</td>");
                out.print("<td>" + job.getSkillRequirement() + "</td>");
                out.print("<td>" + job.getHoursPerWeek() + "</td>");
                out.print("<td>" + job.getCompensation() + "</td>");
                out.print("<td>" + job.getMaxHire() + "</td>");

                String status = job.getStatus();
                if ("Open".equalsIgnoreCase(status)) {
                    out.print("<td class='status-open'>" + status + "</td>");
                } else if ("Filled".equalsIgnoreCase(status)) {
                    out.print("<td class='status-filled'>" + status + "</td>");
                } else {
                    out.print("<td>" + status + "</td>");
                }

                out.print("</tr>");
            }
        %>
    </table>
    <%
        }
    %>

<form class="logout-form" method="post" action="${pageContext.request.contextPath}/mo/logout">
    <button type="submit" class="logout-btn">Logout</button>
</form>

</body>
</html>
