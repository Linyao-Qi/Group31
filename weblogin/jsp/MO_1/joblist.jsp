<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.Job" %>
<%@ page import="com.MoService" %>
<%@ page import="com.AuthUtil" %>
<html>
<head>
    <title>TA Job List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            width: min(1400px, calc(100vw - 40px));
            margin: 50px auto;
            padding: 0 20px;
            background: #f8fafc;
            color: #1f2937;
            overflow-x: hidden;
        }
        .nav {display: flex; justify-content: center; gap: 8px; flex-wrap: nowrap; width: min(980px, calc(100vw - 40px)); margin: 0 0 30px 50%; padding: 10px; background: #eff6ff; border-radius: 8px; transform: translateX(-50%);}
        .nav a {padding: 9px 14px; color: #1d4ed8; text-decoration: none; font-weight: bold; border-radius: 6px;}
        .nav a:hover {background: #dbeafe; text-decoration: none;}
        .job-list-panel {width: 100%; max-width: 100%; margin: 0 auto; overflow-x: hidden;}
        table {width: 100%; max-width: 100%; table-layout: fixed; border-collapse: collapse; margin-top: 20px; background: #fff;}
        th, td {border: 1px solid #ddd; padding: 10px 8px; text-align: center; overflow-wrap: anywhere; word-break: break-word; white-space: normal;}
        th {background-color: #f8fafc; font-size: 14px; font-weight: bold;}
        th:nth-child(1), td:nth-child(1) {width: 10%;}
        th:nth-child(2), td:nth-child(2) {width: 6%;}
        th:nth-child(3), td:nth-child(3) {width: 10%;}
        th:nth-child(4), td:nth-child(4) {width: 6%;}
        th:nth-child(5), td:nth-child(5) {width: 15%;}
        th:nth-child(6), td:nth-child(6) {width: 14%;}
        th:nth-child(7), td:nth-child(7) {width: 7%;}
        th:nth-child(8), td:nth-child(8) {width: 9%;}
        th:nth-child(9), td:nth-child(9) {width: 5%;}
        th:nth-child(10), td:nth-child(10) {width: 6%;}
        th:nth-child(11), td:nth-child(11) {width: 12%;}
        h2 {text-align: center; font-size: 24px; margin-bottom: 24px;}
        .empty {text-align: center; margin-top: 30px; color: #666; font-size: 16px;}
        .current-mo {width: 100%; box-sizing: border-box; margin: 0 auto 26px; padding: 14px; border-radius: 6px; background: #dbeafe; color: #1e3a8a; text-align: center; font-size: 20px;}
        .logout-form {margin: 28px auto 0; width: min(560px, 100%);}
        .logout-btn {width: 100%; padding: 12px; background: #94a3b8; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;}
        .logout-btn:hover {background: #7c8da3;}
        .status-open {color: #16a34a; font-weight: bold;}
        .status-filled {color: #dc2626; font-weight: bold;}
        .status-closed {color: #94a3b8; font-weight: bold;}

        /* ========== 按钮优化：变大 + 去掉下划线 ========== */
        .opt-btn{
            padding: 6px 12px;
            margin: 0 3px;
            border:none;
            border-radius:4px;
            cursor:pointer;
            font-size:14px;
            font-weight:bold;
        }
        a { text-decoration: none !important; }
        .edit-btn{background:#3b82f6;color:#fff;}
        .close-btn{background:#ef4444;color:#fff;}
        .open-btn{background:#16a34a;color:#fff;}
        .save-btn{background:#10b981;color:#fff;}
        .cancel-edit{background:#6b7280;color:#fff;}

        input,textarea{width:90%;padding:4px;border:1px solid #ccc;border-radius:3px;box-sizing:border-box;}
    </style>
</head>
<body>
<%
    MoService.init(getServletContext());
    AuthUtil.init(getServletContext());
    MoService moService = new MoService();
    String currentMoId = (String) session.getAttribute("userId");
    if (!"MO".equals(session.getAttribute("userType")) || currentMoId == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }

    String action = request.getParameter("action");
    String jobId = request.getParameter("jobId");

    if ("save".equals(action)) {
        String subject = request.getParameter("subject");
        String workType = request.getParameter("workType");
        String desc = request.getParameter("description");
        String skill = request.getParameter("skillRequirement");
        String hours = request.getParameter("hoursPerWeek");
        String compensation = request.getParameter("compensation");
        String maxHire = request.getParameter("maxHire");
        moService.updateJobInfo(jobId, subject, workType, desc, skill, hours, compensation, maxHire);
        response.sendRedirect("joblist.jsp");
        return;
    }
    if ("close".equals(action)) {
        moService.updateJobStatus(jobId, "CLOSED");
        response.sendRedirect("joblist.jsp");
        return;
    }
    if ("reopen".equals(action)) {
        moService.updateJobStatus(jobId, "OPEN");
        response.sendRedirect("joblist.jsp");
        return;
    }

    List<Job> allJobs = moService.getAllJobs();
    List<Job> jobList = new ArrayList<>();
    if (allJobs != null) {
        for (Job job : allJobs) {
            if (currentMoId.equals(job.getMoId())) {
                jobList.add(job);
            }
        }
    }
    String editJobId = request.getParameter("editJobId");
%>

<div class="nav">
    <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
    <a href="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp">Hire Applicant</a>
    <a href="${pageContext.request.contextPath}/jsp/MO_1/joblist.jsp">Job List</a>
    <a href="${pageContext.request.contextPath}/moApplicantReview">Skill Match Score</a>
</div>

<h2>TA Job List</h2>
<div class="job-list-panel">
    <div class="current-mo">Current MO: <%= currentMoId %></div>
    <% if (jobList == null || jobList.isEmpty()) { %>
        <div class='empty'>No jobs available yet. Please publish a job first.</div>
    <% } else { %>
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
            <th>Operation</th>
        </tr>
        <% for (Job job : jobList) {
            boolean isEdit = job.getJobId().equals(editJobId);
            String status = job.getStatus() == null ? "" : job.getStatus().toUpperCase();
        %>
        <tr>
            <% if (isEdit) { %>
            <form method="get" action="joblist.jsp">
                <input type="hidden" name="action" value="save">
                <input type="hidden" name="jobId" value="<%=job.getJobId()%>">
                <td><%=job.getJobId()%></td>
                <td><%=job.getMoId()%></td>
                <td><input type="text" name="subject" value="<%=job.getSubject()%>"></td>
                <td><input type="text" name="workType" value="<%=job.getWorkType()%>"></td>
                <td><textarea name="description" rows="2"><%=job.getDescription()%></textarea></td>
                <td><textarea name="skillRequirement" rows="2"><%=job.getSkillRequirement()%></textarea></td>
                <td><input type="text" name="hoursPerWeek" value="<%=job.getHoursPerWeek()%>"></td>
                <td><input type="text" name="compensation" value="<%=job.getCompensation()%>"></td>
                <td><input type="text" name="maxHire" value="<%=job.getMaxHire()%>"></td>
                <td class="
                    <%= "OPEN".equals(status) ? "status-open" :
                       "FILLED".equals(status) ? "status-filled" : "status-closed" %>">
                    <%=status%>
                </td>
                <td>
                    <button type="submit" class="opt-btn save-btn">Save</button>
                    <button type="button" class="opt-btn cancel-edit" onclick="location.href='joblist.jsp'">Cancel</button>
                </td>
            </form>
            <% } else { %>
            <td><%=job.getJobId()%></td>
            <td><%=job.getMoId()%></td>
            <td><%=job.getSubject()%></td>
            <td><%=job.getWorkType()%></td>
            <td><%=job.getDescription()%></td>
            <td><%=job.getSkillRequirement()%></td>
            <td><%=job.getHoursPerWeek()%></td>
            <td><%=job.getCompensation()%></td>
            <td><%=job.getMaxHire()%></td>
            <td class="
                <%= "OPEN".equals(status) ? "status-open" :
                   "FILLED".equals(status) ? "status-filled" : "status-closed" %>">
                <%=status%>
            </td>
            <td>
                <% if (!"CLOSED".equals(status)) { %>
                    <button class="opt-btn edit-btn" onclick="location.href='joblist.jsp?editJobId=<%=job.getJobId()%>'">Edit</button>
                    <button class="opt-btn close-btn" onclick="if(confirm('Close this job?'))location.href='joblist.jsp?action=close&jobId=<%=job.getJobId()%>'">Close</button>
                <% } else { %>
                    <button class="opt-btn open-btn" onclick="location.href='joblist.jsp?action=reopen&jobId=<%=job.getJobId()%>'">Reopen</button>
                <% } %>
            </td>
            <% } %>
        </tr>
        <% } %>
    </table>
    <% } %>
</div>

<form class="logout-form" method="post" action="${pageContext.request.contextPath}/mo/logout">
    <button type="submit" class="logout-btn">Logout</button>
</form>
</body>
</html>
