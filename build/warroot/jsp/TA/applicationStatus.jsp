<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.Application, com.Job, java.util.List, java.util.Map" %>
<html>
<head>
    <title>My Applications</title>
    <style>
        :root {
            --ink: #1e293b;
            --muted: #475569;
            --brand: #2563eb;
            --brand-2: #1d4ed8;
            --surface: #ffffff;
            --line: #e2e8f0;
        }
        body { font-family: "Segoe UI", "Trebuchet MS", sans-serif; max-width: 1200px; margin: 28px auto; padding: 0 20px 24px; color: var(--ink); background: #f8fafc; }
        .nav { margin-bottom: 18px; text-align: center; background: #ffffff; border: 1px solid var(--line); border-radius: 10px; padding: 12px 10px; }
        .nav a { margin: 0 12px; color: #1e40af; text-decoration: none; font-size: 15px; font-weight: 600; }
        .nav a:hover { color: #1d4ed8; text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #1e40af; font-size: 15px; cursor: pointer; padding: 0; margin: 0 12px; font-weight: 600; }
        .nav button:hover { color: #1d4ed8; text-decoration: underline; }
        h2 { color: #0f172a; margin: 0 0 14px; font-size: 26px; }
        table { width: 100%; border-collapse: separate; border-spacing: 0; margin-top: 8px; background: var(--surface); border: 1px solid var(--line); border-radius: 10px; overflow: hidden; box-shadow: 0 6px 16px rgba(15,23,42,0.06); }
        th, td { border-bottom: 1px solid #e2e8f0; padding: 11px 12px; text-align: center; font-size: 14px; }
        th { background: #eff6ff; color: #1e3a8a; font-weight: 700; }
        tr:last-child td { border-bottom: none; }
        tr:nth-child(even) td { background: #f8fafc; }
        tr:hover td { background: #f1f5f9; }
        td.desc { text-align: left; max-width: 200px; word-break: break-word; }
        .status-pending  { color: #d97706; font-weight: bold; }
        .status-accepted { color: #16a34a; font-weight: bold; }
        .status-rejected { color: #dc2626; font-weight: bold; }
        .status-canceled, .status-cancelled { color: #64748b; font-weight: bold; }
        .status-unknown { color: #475569; font-weight: bold; }
        .btn-withdraw { padding: 5px 14px; background: #ef4444; color: white; border: none; border-radius: 8px; font-size: 13px; cursor: pointer; font-weight: 700; }
        .btn-withdraw:hover { background: #dc2626; }
        .empty { text-align: center; color: #334155; padding: 46px 24px; font-size: 16px; border: 1px dashed #cbd5e1; border-radius: 10px; background: #ffffff; }
        .msg   { background: #dcfce7; color: #166534; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; }
        .error { background: #fee2e2; color: #991b1b; padding: 10px 14px; border-radius: 6px; margin-bottom: 18px; font-size: 14px; }
        .legend { margin-bottom: 14px; font-size: 0; }
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
        <span class="status-canceled">CANCELED</span><span>— canceled by admin &nbsp;&nbsp;|&nbsp;&nbsp; </span>
        <span class="status-pending">PENDING</span><span>— awaiting MO review &nbsp;&nbsp;|&nbsp;&nbsp; </span>
        <span class="status-accepted">ACCEPTED</span><span>— hired &nbsp;&nbsp;|&nbsp;&nbsp; </span>
        <span class="status-rejected">REJECTED</span><span>— not selected</span>
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
            String status = app.getAppStatus() == null ? "" : app.getAppStatus().trim().toUpperCase();
            if (status.isEmpty()) status = "UNKNOWN";
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
