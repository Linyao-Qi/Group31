<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.Job, java.util.List" %>
<html>
<head>
    <title>Job List</title>
    <style>
        body { font-family: Arial, sans-serif; max-width: 1200px; margin: 40px auto; padding: 0 20px; }
        .nav { margin-bottom: 30px; text-align: center; }
        .nav a { margin: 0 12px; color: #2563eb; text-decoration: none; font-size: 15px; }
        .nav a:hover { text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #2563eb; font-size: 15px; cursor: pointer; padding: 0; margin: 0 12px; }
        .nav button:hover { text-decoration: underline; }
        h2 { color: #1e293b; }
        .filter-bar { display: flex; gap: 12px; align-items: flex-end; flex-wrap: wrap; margin-bottom: 24px; padding: 16px; background: #f8fafc; border: 1px solid #ddd; border-radius: 6px; }
        .filter-bar label { font-size: 13px; color: #374151; display: block; margin-bottom: 4px; }
        .filter-bar input, .filter-bar select { padding: 7px 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }
        .btn-filter { padding: 8px 20px; background: #2563eb; color: white; border: none; border-radius: 4px; font-size: 14px; cursor: pointer; }
        .btn-filter:hover { background: #1d4ed8; }
        .btn-clear { padding: 8px 16px; background: #f1f5f9; color: #374151; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; cursor: pointer; text-decoration: none; }
        table { width: 100%; border-collapse: collapse; margin-top: 8px; }
        th, td { border: 1px solid #ddd; padding: 10px 12px; text-align: left; font-size: 14px; }
        th { background: #f8fafc; font-weight: bold; }
        td.desc { max-width: 220px; word-break: break-word; }
        .btn-apply { padding: 6px 14px; background: #16a34a; color: white; border: none; border-radius: 4px; font-size: 13px; cursor: pointer; text-decoration: none; display: inline-block; }
        .btn-apply:hover { background: #15803d; }
        .empty { text-align: center; color: #64748b; padding: 40px; font-size: 15px; }
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

    <h2>Available TA Jobs</h2>

    <!-- Filter Form -->
    <form method="get" action="${pageContext.request.contextPath}/ta/jobs">
        <div class="filter-bar">
            <div>
                <label>Subject</label>
                <input type="text" name="subject" value="${filterSubject}" placeholder="e.g. Java">
            </div>
            <div>
                <label>Work Type</label>
                <select name="workType">
                    <option value="">All Types</option>
                    <option value="Lab"    <%= "Lab".equals(request.getAttribute("filterWorkType"))    ? "selected" : "" %>>Lab</option>
                    <option value="Tutorial" <%= "Tutorial".equals(request.getAttribute("filterWorkType")) ? "selected" : "" %>>Tutorial</option>
                    <option value="Marking" <%= "Marking".equals(request.getAttribute("filterWorkType"))  ? "selected" : "" %>>Marking</option>
                    <option value="Lecture" <%= "Lecture".equals(request.getAttribute("filterWorkType"))  ? "selected" : "" %>>Lecture</option>
                </select>
            </div>
            <div>
                <label>Keyword</label>
                <input type="text" name="keyword" value="${keyword}" placeholder="Search description / skills">
            </div>
            <button type="submit" class="btn-filter">Search</button>
            <a href="${pageContext.request.contextPath}/ta/jobs" class="btn-clear">Clear</a>
        </div>
    </form>

    <%
        @SuppressWarnings("unchecked")
        List<Job> jobs = (List<Job>) request.getAttribute("jobs");
        if (jobs == null || jobs.isEmpty()) {
    %>
        <div class="empty">No open jobs found matching your criteria.</div>
    <% } else { %>
    <table>
        <tr>
            <th>Subject</th>
            <th>Work Type</th>
            <th>Description</th>
            <th>Required Skills</th>
            <th>Hours/Week</th>
            <th>Compensation</th>
            <th>Action</th>
        </tr>
        <% for (Job job : jobs) { %>
        <tr>
            <td><%= job.getSubject() %></td>
            <td><%= job.getWorkType() %></td>
            <td class="desc"><%= job.getDescription() %></td>
            <td><%= job.getSkillRequirement() %></td>
            <td><%= job.getHoursPerWeek() %>h</td>
            <td><%= job.getCompensation() %></td>
            <td>
                <a href="${pageContext.request.contextPath}/ta/apply?jobId=<%= job.getJobId() %>" class="btn-apply">Apply</a>
            </td>
        </tr>
        <% } %>
    </table>
    <% } %>
</body>
</html>
