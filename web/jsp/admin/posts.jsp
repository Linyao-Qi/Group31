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
        body { font-family: Arial, sans-serif; margin: 24px; }
        table { border-collapse: collapse; width: 100%; font-size: 13px; }
        th, td { border: 1px solid #ccc; padding: 6px 8px; text-align: left; }
        th { background: #f5f5f5; }
        .controls { margin: 14px 0; }
        .controls form, .controls a { display: inline-block; margin-right: 8px; }
        .msg { padding: 8px 10px; margin: 10px 0; background: #fff4d6; border: 1px solid #f0d58b; }
        .warn { color: #b00020; }
    </style>
</head>
<body>
<h2>ClosePost (Web)</h2>
<div>Open Posts: <strong><%= request.getAttribute("openPostsCount") %></strong></div>

<% if (message != null && !message.isEmpty()) { %>
<div class="msg"><%= message %></div>
<% } %>

<div class="controls">
    <a href="${pageContext.request.contextPath}/admin/posts?refresh=1">Refresh</a>
    <form method="post" action="${pageContext.request.contextPath}/admin/posts">
        <input type="hidden" name="action" value="save"/>
        <button type="submit">Save</button>
    </form>
    <a href="${pageContext.request.contextPath}/admin/home">Back</a>
    <% if (Boolean.TRUE.equals(hasUnsaved)) { %>
    <span class="warn">Unsaved changes</span>
    <% } %>
</div>

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
        <td><%= p.getDisplayStatus() %></td>
        <td>
            <form method="post" action="${pageContext.request.contextPath}/admin/posts">
                <input type="hidden" name="jobId" value="<%= p.getJobId() %>"/>
                <input type="hidden" name="action" value="<%= p.isOpen() ? "close" : "open" %>"/>
                <button type="submit"><%= p.isOpen() ? "Close" : "Open" %></button>
            </form>
        </td>
    </tr>
    <% } %>
    </tbody>
</table>
</body>
</html>
