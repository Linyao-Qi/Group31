<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.Job, java.util.List" %>
<html>
<head>
    <title>Job List</title>
    <style>
        :root {
            --ink: #1e293b;
            --muted: #475569;
            --brand: #0f766e;
            --brand-2: #0284c7;
            --accent: #f59e0b;
            --surface: #ffffff;
            --line: #dbeafe;
        }
        body {
            font-family: "Segoe UI", "Trebuchet MS", sans-serif;
            max-width: 1200px;
            margin: 28px auto;
            padding: 0 20px 24px;
            color: var(--ink);
            background:
                radial-gradient(circle at 15% 10%, #cffafe 0%, transparent 35%),
                radial-gradient(circle at 90% 20%, #fde68a 0%, transparent 30%),
                linear-gradient(145deg, #f0f9ff 0%, #f8fafc 55%, #ecfeff 100%);
        }
        .nav {
            margin-bottom: 18px;
            text-align: center;
            background: rgba(255,255,255,0.75);
            border: 1px solid #dbeafe;
            border-radius: 14px;
            padding: 12px 10px;
            backdrop-filter: blur(4px);
        }
        .nav a { margin: 0 12px; color: #0c4a6e; text-decoration: none; font-size: 15px; font-weight: 600; }
        .nav a:hover { color: #0369a1; text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #0c4a6e; font-size: 15px; cursor: pointer; padding: 0; margin: 0 12px; font-weight: 600; }
        .nav button:hover { color: #0369a1; text-decoration: underline; }
        h2 {
            color: #0f172a;
            margin: 0 0 14px;
            font-size: 28px;
            letter-spacing: 0.2px;
        }
        .filter-bar {
            display: flex;
            gap: 12px;
            align-items: flex-end;
            flex-wrap: wrap;
            margin-bottom: 24px;
            padding: 18px;
            background: linear-gradient(120deg, rgba(255,255,255,0.95), rgba(240,249,255,0.95));
            border: 1px solid var(--line);
            border-radius: 14px;
            box-shadow: 0 10px 24px rgba(15, 23, 42, 0.07);
        }
        .filter-bar label { font-size: 12px; color: var(--muted); display: block; margin-bottom: 5px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.4px; }
        .filter-bar input, .filter-bar select {
            padding: 8px 11px;
            border: 1px solid #cbd5e1;
            border-radius: 9px;
            font-size: 14px;
            background: #fff;
        }
        .btn-filter {
            padding: 9px 20px;
            background: linear-gradient(135deg, var(--brand), var(--brand-2));
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 14px;
            cursor: pointer;
            font-weight: 700;
        }
        .btn-filter:hover { filter: brightness(1.05); }
        .btn-clear { padding: 9px 16px; background: #f8fafc; color: #334155; border: 1px solid #cbd5e1; border-radius: 10px; font-size: 14px; cursor: pointer; text-decoration: none; font-weight: 600; }
        table {
            width: 100%;
            border-collapse: separate;
            border-spacing: 0;
            margin-top: 8px;
            background: var(--surface);
            border: 1px solid var(--line);
            border-radius: 14px;
            overflow: hidden;
            box-shadow: 0 12px 26px rgba(15, 23, 42, 0.08);
        }
        th, td { border-bottom: 1px solid #e2e8f0; padding: 11px 12px; text-align: left; font-size: 14px; }
        th { background: linear-gradient(90deg, #0f766e, #0369a1); color: #f8fafc; font-weight: 700; letter-spacing: 0.3px; }
        tr:nth-child(even):not(.detail-row) td { background: #f8fafc; }
        tr:hover:not(.detail-row) td { background: #ecfeff; }
        .work-pill {
            display: inline-block;
            padding: 4px 10px;
            border-radius: 999px;
            font-weight: 700;
            font-size: 12px;
            color: #0c4a6e;
            background: linear-gradient(120deg, #bae6fd, #a7f3d0);
        }
        .btn-detail { padding: 6px 12px; background: linear-gradient(120deg, #0284c7, #0ea5e9); color: white; border: none; border-radius: 8px; font-size: 13px; cursor: pointer; font-weight: 700; }
        .btn-detail:hover { filter: brightness(1.06); }
        .btn-apply { padding: 6px 14px; background: linear-gradient(120deg, #22c55e, #16a34a); color: white; border: none; border-radius: 8px; font-size: 13px; cursor: pointer; text-decoration: none; display: inline-block; font-weight: 700; }
        .btn-apply:hover { filter: brightness(1.05); }
        .detail-row { display: none; }
        .detail-cell { background: linear-gradient(90deg, #f0f9ff, #f8fafc) !important; color: #334155; }
        .detail-title { font-weight: bold; margin-right: 8px; }
        .empty {
            text-align: center;
            color: #334155;
            padding: 46px 24px;
            font-size: 16px;
            border: 1px dashed #93c5fd;
            border-radius: 12px;
            background: rgba(255,255,255,0.7);
        }
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
            <th>Required Skills</th>
            <th>Hours/Week</th>
            <th>Compensation</th>
            <th>Details</th>
            <th>Action</th>
        </tr>
        <% for (Job job : jobs) { %>
        <tr>
            <td><%= job.getSubject() %></td>
            <td><span class="work-pill"><%= job.getWorkType() %></span></td>
            <td><%= job.getSkillRequirement() %></td>
            <td><%= job.getHoursPerWeek() %>h</td>
            <td><%= job.getCompensation() %></td>
            <td>
                <button type="button" class="btn-detail" onclick="toggleDetails('detail-<%= job.getJobId() %>')">View Details</button>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/ta/apply?jobId=<%= job.getJobId() %>" class="btn-apply">Apply</a>
            </td>
        </tr>
        <tr id="detail-<%= job.getJobId() %>" class="detail-row">
            <td colspan="7" class="detail-cell">
                <span class="detail-title">Description:</span>
                <span><%= job.getDescription() %></span>
            </td>
        </tr>
        <% } %>
    </table>
    <% } %>
    <script>
        function toggleDetails(rowId) {
            var row = document.getElementById(rowId);
            if (!row) return;
            row.style.display = row.style.display === "table-row" ? "none" : "table-row";
        }
    </script>
</body>
</html>
