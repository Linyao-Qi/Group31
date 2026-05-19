<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.Application, com.SkillMatchUtil, java.util.List" %>
<%
    if (request.getAttribute("apps") == null) {
        response.sendRedirect(request.getContextPath() + "/moApplicantReview");
        return;
    }
%>
<html>
<head>
    <title>All Applications</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 980px;
            margin: 50px auto;
            padding: 0 20px;
            background: #f8fafc;
            color: #1f2937;
        }
        .nav {
            display: flex;
            justify-content: center;
            gap: 8px;
            flex-wrap: wrap;
            margin: 0 auto 30px;
            padding: 10px;
            background: #eff6ff;
            border: 1px solid #bfdbfe;
            border-radius: 8px;
            box-shadow: 0 6px 18px rgba(37, 99, 235, 0.10);
        }
        .nav a {
            padding: 9px 14px;
            color: #1d4ed8;
            text-decoration: none;
            font-weight: bold;
            border-radius: 6px;
        }
        .nav a:hover {
            background: #dbeafe;
            text-decoration: none;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background: #fff;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
        }
        th, td {
            border-bottom: 1px solid #e5e7eb;
            padding: 12px;
            text-align: center;
        }
        th {
            background-color: #eff6ff;
            color: #1e3a8a;
            font-size: 14px;
        }
        .success { color: green; font-weight: bold; text-align: center; }
        .error { color: red; font-weight: bold; text-align: center; }
        .score-pill {
            display: inline-block;
            min-width: 58px;
            padding: 5px 10px;
            border-radius: 999px;
            background: #dbeafe;
            color: #1d4ed8;
            font-weight: bold;
        }
        .details-btn {
            margin-left: 8px;
            padding: 6px 10px;
            border: 1px solid #93c5fd;
            border-radius: 4px;
            background: #ffffff;
            color: #2563eb;
            cursor: pointer;
        }
        .details-btn:hover {
            background: #eff6ff;
        }
        .detail-row {
            display: none;
            background: #fbfdff;
        }
        .detail-cell {
            text-align: left;
            padding: 16px 20px;
        }
        .detail-title {
            margin: 0 0 8px;
            font-weight: bold;
            color: #334155;
        }
        .skill-chip {
            display: inline-block;
            margin: 3px 6px 3px 0;
            padding: 5px 9px;
            border-radius: 999px;
            font-size: 13px;
        }
        .matched-chip {
            background: #dbeafe;
            color: #1e3a8a;
        }
        .missing-chip {
            background: #fee2e2;
            color: #991b1b;
        }
        .muted {
            color: #64748b;
            font-size: 13px;
        }
        .current-mo {
            margin: 0 auto 26px;
            padding: 14px;
            border-radius: 6px;
            background: #dbeafe;
            color: #1e3a8a;
            text-align: center;
            font-size: 20px;
        }
        .logout-form {margin: 28px auto 0; width: min(560px, 100%);}
        .logout-btn {width: 100%; padding: 12px; background: #94a3b8; color: white; border: none; border-radius: 5px; cursor: pointer; font-size: 16px;}
        .logout-btn:hover {background: #7c8da3;}
    </style>
    <script>
        function toggleDetails(id) {
            var row = document.getElementById(id);
            if (!row) return;
            row.style.display = row.style.display === "table-row" ? "none" : "table-row";
        }
    </script>
</head>
<body>

    <div class="nav">
        <a href="${pageContext.request.contextPath}/jsp/MO_1/publishJob.jsp">Publish Job</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/moApplicantList.jsp">Hire Applicant</a>
        <a href="${pageContext.request.contextPath}/jsp/MO_1/appList.jsp">Application List</a>
        <a href="${pageContext.request.contextPath}/moApplicantReview">Skill Match Score</a>
    </div>

    <h2>All Applications</h2>

    <%
        String msg = (String) request.getAttribute("msg");
        String msgType = (String) request.getAttribute("msgType");
        String currentMoId = (String) session.getAttribute("userId");
    %>
    <div class="current-mo">Current MO: <%= currentMoId %></div>

    <% if (msg != null) { %>
        <div class="<%= msgType %>"><%= msg %></div>
    <% } %>

    <table>
        <tr>
            <th>Application ID</th>
            <th>Job ID</th>
            <th>TA ID</th>
            <th>Status</th>
            <th>Match Score</th>
        </tr>
        <%
        List<Application> apps = (List<Application>) request.getAttribute("apps");
        List<SkillMatchUtil.MatchResult> matchResults =
                (List<SkillMatchUtil.MatchResult>) request.getAttribute("matchResults");
        
        if (apps != null && !apps.isEmpty()) {
            for (int i = 0; i < apps.size(); i++) {
                Application app = apps.get(i);
                SkillMatchUtil.MatchResult result = matchResults.get(i);
                String detailId = "skillDetail" + i;
        %>
        <tr>
            <td><%= app.getAppId() %></td>
            <td><%= app.getJobId() %></td>
            <td><%= app.getTaId() %></td>
            <td><%= app.getAppStatus() %></td>
            <td>
                <span class="score-pill"><%= result.getScore() %> pts</span>
                <button class="details-btn" type="button" onclick="toggleDetails('<%= detailId %>')">Details</button>
            </td>
        </tr>
        <tr id="<%= detailId %>" class="detail-row">
            <td colspan="5" class="detail-cell">
                <div class="detail-title">Matched Skills</div>
                <% if (result.getMatchedSkills().isEmpty()) { %>
                    <span class="muted">No required skills matched.</span>
                <% } else {
                    for (String skill : result.getMatchedSkills()) {
                %>
                    <span class="skill-chip matched-chip"><%= skill %></span>
                <%  }
                   } %>

                <div class="detail-title" style="margin-top:12px;">Missing Skills</div>
                <% if (result.getMissingSkills().isEmpty()) { %>
                    <span class="muted">All required skills are covered.</span>
                <% } else {
                    for (String skill : result.getMissingSkills()) {
                %>
                    <span class="skill-chip missing-chip"><%= skill %></span>
                <%  }
                   } %>
            </td>
        </tr>
        <%
            }
        } else {
        %>
        <tr>
            <td colspan="5">No applications yet.</td>
        </tr>
        <%
        }
        %>
    </table>
    <form class="logout-form" method="post" action="${pageContext.request.contextPath}/mo/logout">
        <button type="submit" class="logout-btn">Logout</button>
    </form>

</body>
</html>
