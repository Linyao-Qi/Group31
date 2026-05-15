<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Admin.AdminRecruitment" %>
<%
    List<AdminRecruitment> posts = (List<AdminRecruitment>) request.getAttribute("posts");
    String message = (String) request.getAttribute("message");
    Boolean hasUnsaved = (Boolean) request.getAttribute("hasUnsavedChanges");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Posts</title>
    <style>
        * { box-sizing: border-box; }
        body { margin: 0; background: #f3f6fb; color: #1f2937; font-family: Arial, Helvetica, sans-serif; font-size: 14px; }
        .page { width: min(1180px, calc(100% - 48px)); margin: 0 auto; padding: 32px 0 40px; }
        .nav { display: flex; align-items: center; justify-content: space-between; gap: 16px; padding: 12px 14px; margin-bottom: 18px; background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06); }
        .brand { font-weight: 800; color: #111827; white-space: nowrap; }
        .nav-links { display: flex; align-items: center; justify-content: flex-end; flex-wrap: wrap; gap: 8px; }
        .nav a, .nav button { display: inline-flex; align-items: center; justify-content: center; min-height: 34px; padding: 8px 11px; border-radius: 9px; border: 1px solid transparent; background: transparent; color: #374151; font: inherit; text-decoration: none; cursor: pointer; }
        .nav a:hover, .nav button:hover { background: #eff6ff; color: #1d4ed8; }
        .nav .active { background: #2563eb; color: #fff; }
        .nav .active:hover { background: #1d4ed8; color: #fff; }
        .header { display: flex; align-items: flex-start; justify-content: space-between; gap: 18px; margin-bottom: 18px; }
        h2 { margin: 0; font-size: 28px; color: #111827; }
        .subtitle { margin: 7px 0 0; color: #6b7280; }
        .card { background: #fff; border: 1px solid #e5e7eb; border-radius: 14px; box-shadow: 0 8px 22px rgba(15, 23, 42, 0.06); padding: 22px; margin-bottom: 18px; }
        .stat { border: 1px solid #e5e7eb; border-radius: 12px; background: #f8fbff; padding: 14px; min-width: 170px; }
        .stat-label { color: #6b7280; font-size: 13px; margin-bottom: 8px; }
        .stat-value { color: #111827; font-size: 24px; line-height: 1; font-weight: 800; }
        .controls { display: flex; align-items: center; flex-wrap: wrap; gap: 10px; }
        .inline-form { display: inline-flex; margin: 0; }
        .btn, button {
            display: inline-flex; align-items: center; justify-content: center; min-height: 38px; padding: 9px 14px;
            border-radius: 9px; border: 1px solid #d1d5db; background: #fff; color: #374151;
            font: inherit; line-height: 1; text-decoration: none; cursor: pointer;
        }
        .btn:hover, button:hover { background: #f9fafb; border-color: #9ca3af; }
        .primary { background: #2563eb; border-color: #2563eb; color: #fff; }
        .primary:hover { background: #1d4ed8; border-color: #1d4ed8; }
        .message { padding: 11px 13px; margin-bottom: 14px; border-radius: 10px; background: #fffbeb; border: 1px solid #fde68a; color: #92400e; }
        .warn { color: #b91c1c; font-weight: 700; }
        .table-wrap { width: 100%; overflow-x: auto; }
        table { width: 100%; border-collapse: separate; border-spacing: 0; font-size: 13px; }
        th, td { border-bottom: 1px solid #e5e7eb; padding: 11px 12px; text-align: left; vertical-align: top; }
        th { background: #eef4ff; color: #374151; font-weight: 700; white-space: nowrap; }
        tbody tr:hover { background: #f9fbff; }
        tbody tr:last-child td { border-bottom: 0; }
        .pill { display: inline-flex; border-radius: 999px; padding: 4px 9px; font-size: 12px; font-weight: 700; white-space: nowrap; }
        .open { background: #dcfce7; color: #166534; }
        .closed { background: #e5e7eb; color: #374151; }
        @media (max-width: 720px) { .page { width: calc(100% - 28px); padding-top: 22px; } .header, .nav { flex-direction: column; } }
    </style>
</head>
<body>
<div class="page">
    <div class="nav">
        <div class="brand">Admin System</div>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/admin/home">Home</a>
            <a href="${pageContext.request.contextPath}/admin/workloads">CheckWorkload</a>
            <a class="active" href="${pageContext.request.contextPath}/admin/posts">ClosePost</a>
            <a href="${pageContext.request.contextPath}/admin/report">Recruitment Report</a>
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/logout">
                <button type="submit">Logout</button>
            </form>
        </div>
    </div>

    <div class="header">
        <div>
            <h2>ClosePost</h2>
            <p class="subtitle">Review recruitment posts and open or close positions.</p>
        </div>
        <div class="stat">
            <div class="stat-label">Open Posts</div>
            <div class="stat-value"><%= request.getAttribute("openPostsCount") %></div>
        </div>
    </div>

    <% if (message != null && !message.isEmpty()) { %>
    <div class="message"><%= message %></div>
    <% } %>

    <div class="card">
        <div class="controls">
            <a class="btn" href="${pageContext.request.contextPath}/admin/posts?refresh=1">Refresh</a>
            <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/posts">
                <input type="hidden" name="action" value="save"/>
                <button class="primary" type="submit">Save</button>
            </form>
            <a class="btn" href="${pageContext.request.contextPath}/admin/home">Back</a>
            <% if (Boolean.TRUE.equals(hasUnsaved)) { %>
            <span class="warn">Unsaved changes</span>
            <% } %>
        </div>
    </div>

    <div class="card">
        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Job ID</th>
                    <th>MO ID</th>
                    <th>Subject</th>
                    <th>Work Type</th>
                    <th>Description</th>
                    <th>Skill Requirement</th>
                    <th>Hours/Week</th>
                    <th>Compensation</th>
                    <th>Status</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <% for (AdminRecruitment p : posts) { %>
                <tr>
                    <td><%= p.getJobId() %></td>
                    <td><%= p.getMoId() %></td>
                    <td><%= p.getSubject() %></td>
                    <td><%= p.getWorkType() %></td>
                    <td><%= p.getDescription() %></td>
                    <td><%= p.getRequirements() %></td>
                    <td><%= p.getHoursPerWeek() %></td>
                    <td><%= p.getCompensation() %></td>
                    <td><span class="pill <%= p.isOpen() ? "open" : "closed" %>"><%= p.getDisplayStatus() %></span></td>
                    <td>
                        <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/posts">
                            <input type="hidden" name="jobId" value="<%= p.getJobId() %>"/>
                            <input type="hidden" name="action" value="<%= p.isOpen() ? "close" : "open" %>"/>
                            <button class="<%= p.isOpen() ? "" : "primary" %>" type="submit"><%= p.isOpen() ? "Close" : "Open" %></button>
                        </form>
                    </td>
                </tr>
                <% } %>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
