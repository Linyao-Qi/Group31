<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%@ page import="TA.ResumeSuggestion" %>

<html>

<head>

    <title>Resume Suggestion</title>

    <style>

        :root {
            --ink: #1e293b;
            --muted: #475569;
            --brand: #2563eb;
            --line: #e2e8f0;
            --bg: #f8fafc;
        }

        body {
            font-family: "Segoe UI", "Trebuchet MS", sans-serif;
            max-width: 1100px;
            margin: 28px auto;
            padding: 0 20px 24px;
            color: var(--ink);
            background: var(--bg);
        }

        .nav {
            margin-bottom: 24px;
            text-align: center;
            background: white;
            border: 1px solid var(--line);
            border-radius: 10px;
            padding: 12px 10px;
        }

        .nav a {
            margin: 0 12px;
            color: #1e40af;
            text-decoration: none;
            font-size: 15px;
            font-weight: 600;
        }

        .nav a:hover {
            color: #1d4ed8;
            text-decoration: underline;
        }

        h1 {
            text-align: center;
            margin-bottom: 8px;
            color: #0f172a;
        }

        .subtitle {
            text-align: center;
            color: var(--muted);
            margin-bottom: 32px;
        }

        .container {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 24px;
        }

        .card {
            background: white;
            border: 1px solid var(--line);
            border-radius: 12px;
            padding: 24px;
            box-shadow: 0 6px 16px rgba(15,23,42,0.06);
        }

        .score {
            text-align: center;
            font-size: 52px;
            font-weight: bold;
            color: #2563eb;
            margin-top: 10px;
        }

        h2 {
            margin-top: 0;
            color: #1e40af;
            font-size: 20px;
        }

        ul {
            padding-left: 20px;
        }

        li {
            margin-bottom: 10px;
            color: #334155;
        }

        .full-width {
            grid-column: 1 / span 2;
        }

    </style>

</head>

<body>

<div class="nav">

    <a href="${pageContext.request.contextPath}/ta/home">Home</a>

    <a href="${pageContext.request.contextPath}/ta/jobs">Job List</a>

    <a href="${pageContext.request.contextPath}/ta/status">My Applications</a>

    <a href="${pageContext.request.contextPath}/ta/profile">Profile</a>

</div>

<%
    ResumeSuggestion suggestion =
            (ResumeSuggestion)
                    request.getAttribute("suggestion");
%>

<h1>Resume Analysis</h1>

<p class="subtitle">
    AI-assisted CV improvement suggestions
</p>

<div class="container">

    <div class="card">

        <h2>Match Score</h2>

        <div class="score">
            <%= suggestion.getScore() %>%
        </div>

    </div>

    <div class="card">

        <h2>Matched Skills</h2>

        <ul>

            <%
                for (String skill :
                        suggestion.getMatchedSkills()) {
            %>

            <li><%= skill %></li>

            <%
                }
            %>

        </ul>

    </div>

    <div class="card">

        <h2>Missing Skills</h2>

        <ul>

            <%
                for (String skill :
                        suggestion.getMissingSkills()) {
            %>

            <li><%= skill %></li>

            <%
                }
            %>

        </ul>

    </div>

    <div class="card full-width">

        <h2>Suggestions</h2>

        <ul>

            <%
                for (String s :
                        suggestion.getSuggestions()) {
            %>

            <li><%= s %></li>

            <%
                }
            %>

        </ul>

    </div>

</div>

</body>

</html>