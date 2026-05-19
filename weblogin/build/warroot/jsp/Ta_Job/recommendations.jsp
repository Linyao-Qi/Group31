<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="TaJob.JobPosting" %>
<%@ page import="TaJob.JobRecommendation" %>
<%@ page import="TaJob.TaProfileSnapshot" %>
<%
    List<JobRecommendation> recommendations = (List<JobRecommendation>) request.getAttribute("recommendations");
    TaProfileSnapshot profile = (TaProfileSnapshot) request.getAttribute("profile");
    String taId = request.getAttribute("taId") == null ? "" : String.valueOf(request.getAttribute("taId"));
%>
<%!
    private String h(String text) {
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
    <title>TA Job Recommendations</title>
    <style>
        body { font-family: "Segoe UI", Arial, sans-serif; margin: 24px; color: #1f2937; background: #f8fafc; }
        h1 { margin: 0 0 12px; }
        .nav { margin-bottom: 18px; }
        .nav a { margin-right: 14px; color: #1d4ed8; text-decoration: none; font-weight: 600; }
        .panel { background: #fff; border: 1px solid #dbeafe; border-radius: 8px; padding: 16px; margin-bottom: 16px; }
        .btn {
            padding: 9px 14px;
            border: 0;
            border-radius: 6px;
            background: #2563eb;
            color: #fff;
            cursor: pointer;
            text-decoration: none;
            font-size: 14px;
            font-weight: 700;
            display: inline-flex;
            min-width: 86px;
            justify-content: center;
            white-space: nowrap;
        }
        table { width: 100%; border-collapse: collapse; background: #fff; }
        th, td { border: 1px solid #e5e7eb; padding: 10px; text-align: left; font-size: 14px; vertical-align: top; }
        th { background: #eff6ff; }
        .score { font-weight: 800; color: #047857; font-size: 18px; }
        .pill { display: inline-block; padding: 3px 8px; margin: 0 4px 4px 0; border-radius: 999px; font-size: 12px; font-weight: 700; }
        .match { background: #dcfce7; color: #166534; }
        .missing { background: #fef3c7; color: #92400e; }
        .empty { color: #64748b; background: #fff; border: 1px dashed #cbd5e1; border-radius: 8px; padding: 22px; }
    </style>
</head>
<body>
<div class="nav">
    <a href="${pageContext.request.contextPath}/taJobList">Job Browser</a>
    <a href="${pageContext.request.contextPath}/taJobRecommendations">Recommended Jobs</a>
    <a href="${pageContext.request.contextPath}/ta/home">TA Home</a>
</div>

<h1>TA Smart Job Recommendations</h1>

<div class="panel">
    <p>
        Recommendations for TA:
        <strong><%= h(taId) %></strong>
        &nbsp;
        Profile skills:
        <strong><%= profile == null || profile.getSkills().isEmpty() ? "not found or not completed" : h(profile.getSkills()) %></strong>
        &nbsp; Major:
        <strong><%= profile == null || profile.getMajor().isEmpty() ? "not completed" : h(profile.getMajor()) %></strong>
    </p>
</div>

<%
    if (recommendations == null || recommendations.isEmpty()) {
%>
<div class="empty">No open jobs are currently available for recommendation.</div>
<%
    } else {
%>
<table>
    <thead>
    <tr>
        <th>Match Rate</th>
        <th>Job ID</th>
        <th>Subject</th>
        <th>Work Type</th>
        <th>Matched Skills</th>
        <th>Profile Skills to Confirm</th>
        <th>Reason</th>
        <th>Status</th>
        <th style="width: 130px;">Action</th>
    </tr>
    </thead>
    <tbody>
    <%
        for (JobRecommendation recommendation : recommendations) {
            JobPosting job = recommendation.getJob();
    %>
    <tr>
        <td><span class="score"><%= recommendation.getScore() %></span>/100</td>
        <td><%= h(job.getJobId()) %></td>
        <td><%= h(job.getSubject()) %></td>
        <td><%= h(job.getWorkType()) %></td>
        <td>
            <% if (recommendation.getMatchedSkills().isEmpty()) { %>
                -
            <% } else {
                for (String skill : recommendation.getMatchedSkills()) {
            %>
                <span class="pill match"><%= h(skill) %></span>
            <%  }
               } %>
        </td>
        <td>
            <% if (recommendation.getMissingSkills().isEmpty()) { %>
                -
            <% } else {
                for (String skill : recommendation.getMissingSkills()) {
            %>
                <span class="pill missing"><%= h(skill) %></span>
            <%  }
               } %>
        </td>
        <td><%= h(recommendation.getReason()) %></td>
        <td><%= h(job.getStatus()) %></td>
        <td>
            <a class="btn" href="${pageContext.request.contextPath}/ta/jobs?focusJobId=<%= h(job.getJobId()) %>">View Job</a>
        </td>
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
