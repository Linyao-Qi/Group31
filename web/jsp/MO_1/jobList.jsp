<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.Job" %>
<%@ page import="com.MoService" %>
<%@ page import="com.AuthUtil" %>
<html>
<head>
    <title>TA Job List</title>
    <style>
        body {font-family: Arial, sans-serif; max-width: 1100px; margin: 50px auto; padding: 0 20px;}
        table {width: 100%; border-collapse: collapse; margin-top: 20px;}
        th, td {border: 1px solid #ddd; padding: 12px; text-align: center;}
        th {background: #f8fafc; color: #333;}
        .nav {margin-bottom: 30px; text-align: center;}
        .nav a {margin: 0 10px; color: #2563eb; text-decoration: none;}
        .nav a:hover {text-decoration: underline;}
        .empty {text-align: center; margin-top: 30px; color: #666;}
    </style>
</head>
<body>
<%
    MoService.init(getServletContext());
    AuthUtil.init(getServletContext());

    MoService moService = new MoService();
    List<Job> jobList = moService.getAllJobs();
%>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/hireApplicant.jsp">Hire Applicant</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/jobList.jsp">Job List</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">Application List</a>
    </div>
    <h2 align="center">TA Job List</h2>
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
            <th>Max Hire</th> <!-- 新增 -->
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
                out.print("<td>" + job.getStatus() + "</td>");
                out.print("</tr>");
            }
        %>
    </table>
    <%
        }
    %>
</body>
</html>