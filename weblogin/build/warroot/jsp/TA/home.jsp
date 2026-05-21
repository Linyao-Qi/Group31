<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>TA Home</title>
    <style>
        :root {
            --ink: #1e293b;
            --muted: #475569;
            --brand: #2563eb;
            --line: #e2e8f0;
        }
        body { font-family: "Segoe UI", "Trebuchet MS", sans-serif; max-width: 1200px; margin: 28px auto; padding: 0 20px 24px; color: var(--ink); background: #f8fafc; }
        .nav { margin-bottom: 18px; text-align: center; background: #ffffff; border: 1px solid var(--line); border-radius: 10px; padding: 12px 10px; }
        .nav a { margin: 0 12px; color: #1e40af; text-decoration: none; font-size: 15px; font-weight: 600; }
        .nav a:hover { color: #1d4ed8; text-decoration: underline; }
        .nav form { display: inline; }
        .nav button { background: none; border: none; color: #1e40af; font-size: 15px; cursor: pointer; padding: 0; margin: 0 12px; font-weight: 600; }
        .nav button:hover { color: #1d4ed8; text-decoration: underline; }
        h2 { color: #0f172a; font-size: 26px; text-align: center; margin: 40px 0 10px; }
        .welcome { text-align: center; color: var(--muted); margin-bottom: 40px; font-size: 15px; }
        .card-grid { display: flex; gap: 24px; justify-content: center; flex-wrap: wrap; }
        .card {
            background: white; border: 1px solid var(--line); border-radius: 10px;
            padding: 36px 40px; width: 200px; text-align: center;
            box-shadow: 0 6px 16px rgba(15,23,42,0.06);
            text-decoration: none; color: var(--ink); transition: box-shadow 0.2s, border-color 0.2s;
        }
        .card:hover { box-shadow: 0 8px 24px rgba(37,99,235,0.15); border-color: #2563eb; }
        .card .icon { font-size: 38px; margin-bottom: 14px; }
        .card .label { font-size: 15px; font-weight: 700; color: #1e40af; }
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

    <h2>Welcome, <%= session.getAttribute("taId") %></h2>
    <p class="welcome">TA Job Application Portal</p>

    <div class="card-grid">
        <a href="${pageContext.request.contextPath}/ta/jobs" class="card">
            <div class="icon">&#128203;</div>
            <div class="label">Browse Jobs</div>
        </a>
        <a href="${pageContext.request.contextPath}/ta/status" class="card">
            <div class="icon">&#128196;</div>
            <div class="label">My Applications</div>
        </a>
        <a href="${pageContext.request.contextPath}/ta/profile" class="card">
            <div class="icon">&#128100;</div>
            <div class="label">My Profile</div>
        </a>
        <a href="${pageContext.request.contextPath}/ta/resumeSuggestion" class="card">
            <div class="icon">&#128221;</div>
            <div class="label">Resume Suggestion</div>
        </a>
    </div>
</body>
</html>
