<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.Application, com.Job, java.util.List, java.util.Map" %>
<html>
<head>
    <title>My Applications</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1100px; margin: 40px auto; padding: 0 20px; }
        .nav { margin-bottom: 30px; text-align: center; }
        .nav a { margin: 0 12px; color: #2563eb; text-decoration: none; font-size: 15px; }
        .nav a:hover { text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #2563eb; font-size: 15px; cursor: pointer; padding: 0; margin: 0 12px; }
        .nav button:hover { text-decoration: underline; }
        h2 { color: #1e293b; }
        table { width: 100%; border-collapse: collapse; margin-top: 16px; font-size: 14px; }
        th, td { border: 1px solid #ddd; padding: 10px 12px; text-align: center; }
        th { background: #f8fafc; font-weight: bold; }
        td.intro { text-align: left; max-width: 180px; word-break: break-word; }
        td.desc { text-align: left; max-width: 200px; word-break: break-word; }
        .status-pending  { color: #d97706; font-weight: bold; }
        .status-accepted { color: #16a34a; font-weight: bold; }
        .status-rejected { color: #dc2626; font-weight: bold; }
        .status-withdrawn { color: #6b7280; font-weight: bold; }
        .btn-withdraw { padding: 5px 12px; background: #ef4444; color: white; border: none; border-radius: 4px; font-size: 13px; cursor: pointer; }
        .btn-withdraw:hover { background: #dc2626; }
        .empty { text-align: center; color: #64748b; padding: 40px; font-size: 15px; }
        .msg   { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 4px; margin-bottom: 18px; font-size: 14px; }
        .error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 4px; margin-bottom: 18px; font-size: 14px; }
        .legend { margin-bottom: 12px; font-size: 0; color: #64748b; }
        .legend span { font-size: 13px; margin-right: 2px; }
    </style>
</head>
<body>
    <div class="nav">
        <a href="${pageContext.request.contextPath}/ta/home">Home</a>
        <a href="${pageContext.request.contextPath}/ta/jobs">Job List</a>
        <a href="${pageContext.request.contextPath}/ta/status">My Applications</a>
        <a href="${pageContext.request.contextPath}/ta/profile">Profile</a>
        <form method="post" action="${pageContext.request.contextPath}/ta/logout">
            <button type="submit">Logout</button>
        </form>
    </div>

    <h2>My Applications</h2>

    <% if (request.getAttribute("msg") != null) { %>
        <div class="msg">${msg}</div>
    <% } %>
    <% if (request.getAttribute("error") != null) { %>
        <div class="error">${error}</div>
    <% } %>

    <div class="legend">
        Status: &nbsp;&nbsp;
        <span class="status-pending">PENDING</span><span>— awaiting MO review &nbsp;&nbsp;|&nbsp;&nbsp; </span>
        <span class="status-accepted">ACCEPTED</span><span>— hired &nbsp;&nbsp;|&nbsp;&nbsp; </span>
        <span class="status-rejected">REJECTED</span><span>— not selected &nbsp;&nbsp;|&nbsp;&nbsp; </span>
        <span class="status-withdrawn">WITHDRAWN</span><span>— withdrawn by you</span>
    </div>

    <%
        @SuppressWarnings("unchecked")
        List<Application> apps = (List<Application>) request.getAttribute("apps");
        @SuppressWarnings("unchecked")
        Map<String, Job> jobMap = (Map<String, Job>) request.getAttribute("jobMap");
        if (jobMap == null) jobMap = new java.util.HashMap<>();
        if (apps == null || apps.isEmpty()) {
    %>
        <div class="empty">You have not applied for any jobs yet.
            <a href="${pageContext.request.contextPath}/ta/jobs">Browse Jobs &rarr;</a>
        </div>
    <% } else { %>
    <table>
        <tr>
            <th>Subject</th>
            <th>Work Type</th>
            <th>Hours/Week</th>
            <th>Compensation</th>
            <th>Description</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        <% for (Application app : apps) {
            String status = app.getAppStatus();
            String statusClass = "status-" + status.toLowerCase();
            Job job = jobMap.get(app.getJobId());
            String subject = job != null ? job.getSubject() : app.getJobId();
            String workType = job != null ? job.getWorkType() : "-";
            String hours = job != null ? job.getHoursPerWeek() + "h" : "-";
            String compensation = job != null ? job.getCompensation() : "-";
            String description = job != null ? job.getDescription() : "-";
        %>
        <tr>
            <td><%= subject %></td>
            <td><%= workType %></td>
            <td><%= hours %></td>
            <td><%= compensation %></td>
            <td class="desc"><%= description %></td>
            <td class="<%= statusClass %>"><%= status %></td>
            <td>
                <% if ("PENDING".equals(status)) { %>
                    <form method="post" action="${pageContext.request.contextPath}/ta/withdraw" style="margin:0;">
                        <input type="hidden" name="appId" value="<%= app.getAppId() %>">
                        <button type="submit" class="btn-withdraw"
                                onclick="return confirm('Withdraw this application?');">Withdraw</button>
                    </form>
                <% } else { %>
                    —
                <% } %>
            </td>
        </tr>
        <% } %>
    </table>
    <% } %>
</body>
</html>
