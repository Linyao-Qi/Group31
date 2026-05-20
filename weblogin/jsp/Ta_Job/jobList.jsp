<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="TaJob.JobPosting" %>
<%
    List<JobPosting> jobs = (List<JobPosting>) request.getAttribute("jobs");
    Set<String> subjects = (Set<String>) request.getAttribute("subjects");
    Set<String> workTypes = (Set<String>) request.getAttribute("workTypes");
    Set<String> statuses = (Set<String>) request.getAttribute("statuses");

    String keyword = request.getAttribute("keyword") == null ? "" : String.valueOf(request.getAttribute("keyword"));
    String subject = request.getAttribute("subject") == null ? "" : String.valueOf(request.getAttribute("subject"));
    String workType = request.getAttribute("workType") == null ? "" : String.valueOf(request.getAttribute("workType"));
    String status = request.getAttribute("status") == null ? "" : String.valueOf(request.getAttribute("status"));
%>
<%! private String h(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>TA Job Browser</title>
    <style>
        body { font-family: "Segoe UI", Arial, sans-serif; margin: 24px; color: #1f2937; }
        h1 { margin: 0 0 16px; }
        .search-panel {
            background: #f8fafc;
            border: 1px solid #dbeafe;
            border-radius: 8px;
            padding: 16px;
            margin-bottom: 16px;
        }
        .row { display: flex; gap: 12px; flex-wrap: wrap; align-items: end; }
        .item { display: flex; flex-direction: column; min-width: 180px; }
        label { font-size: 13px; margin-bottom: 6px; color: #334155; }
        input, select {
            padding: 8px 10px;
            border: 1px solid #cbd5e1;
            border-radius: 6px;
            font-size: 14px;
            background: #fff;
        }
        .btn {
            padding: 9px 14px;
            border: 0;
            border-radius: 6px;
            background: #2563eb;
            color: #fff;
            cursor: pointer;
            font-size: 14px;
        }
        .btn-secondary { background: #64748b; text-decoration: none; display: inline-block; }
        table { width: 100%; border-collapse: collapse; background: #fff; }
        th, td { border: 1px solid #e5e7eb; padding: 10px; text-align: left; font-size: 14px; vertical-align: top; }
        th { background: #eff6ff; }
        .empty { margin-top: 12px; color: #64748b; }
    </style>
</head>
<body>
<div style="margin-bottom: 18px;">
    <a href="${pageContext.request.contextPath}/taJobList" style="margin-right: 14px; color: #1d4ed8; text-decoration: none; font-weight: 600;">Job Browser</a>
    <a href="${pageContext.request.contextPath}/ta/home" style="color: #1d4ed8; text-decoration: none; font-weight: 600;">TA Home</a>
</div>
<h1>TA Job Browser</h1>

<form class="search-panel" method="get" action="${pageContext.request.contextPath}/taJobList">
    <div class="row">
        <div class="item" style="min-width: 280px;">
            <label for="keyword">Keyword</label>
            <input id="keyword" type="text" name="keyword" value="<%= h(keyword) %>" placeholder="Search subject, work type, skills, description...">
        </div>

        <div class="item">
            <label for="subject">Subject</label>
            <select id="subject" name="subject">
                <option value="">All</option>
                <%
                    if (subjects != null) {
                        for (String value : subjects) {
                %>
                <option value="<%= h(value) %>" <%= value.equalsIgnoreCase(subject) ? "selected" : "" %>><%= h(value) %></option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <div class="item">
            <label for="workType">Work Type</label>
            <select id="workType" name="workType">
                <option value="">All</option>
                <%
                    if (workTypes != null) {
                        for (String value : workTypes) {
                %>
                <option value="<%= h(value) %>" <%= value.equalsIgnoreCase(workType) ? "selected" : "" %>><%= h(value) %></option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <div class="item">
            <label for="status">Status</label>
            <select id="status" name="status">
                <option value="">All</option>
                <%
                    if (statuses != null) {
                        for (String value : statuses) {
                %>
                <option value="<%= h(value) %>" <%= value.equalsIgnoreCase(status) ? "selected" : "" %>><%= h(value) %></option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <button class="btn" type="submit">Search</button>
        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/taJobList">Reset</a>
    </div>
</form>

<%
    if (jobs == null || jobs.isEmpty()) {
%>
<div class="empty">No jobs found for the selected filters.</div>
<%
    } else {
%>
<table>
    <thead>
    <tr>
        <th>Job ID</th>
        <th>MO ID</th>
        <th>Subject</th>
        <th>Work Type</th>
        <th>Description</th>
        <th>Skill Requirement</th>
        <th>Hours / Week</th>
        <th>Compensation</th>
        <th>Status</th>
    </tr>
    </thead>
    <tbody>
    <%
        for (JobPosting job : jobs) {
    %>
    <tr>
        <td><%= h(job.getJobId()) %></td>
        <td><%= h(job.getMoId()) %></td>
        <td><%= h(job.getSubject()) %></td>
        <td><%= h(job.getWorkType()) %></td>
        <td><%= h(job.getDescription()) %></td>
        <td><%= h(job.getSkillRequirement()) %></td>
        <td><%= h(job.getHoursPerWeek()) %></td>
        <td><%= h(job.getCompensation()) %></td>
        <td><%= h(job.getStatus()) %></td>
    </tr>
    <%
        }
    %>
    </tbody>
</table>
<%
    }
%>
</body>
</html>
